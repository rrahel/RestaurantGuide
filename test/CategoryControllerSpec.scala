import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._
import helpers.RepositoryAwareContext
import models.{User, Category}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.{CategoryRepository}

/**
 * Created by cemirrah13 on 23.01.2016.
 */
class CategoryControllerSpec extends PlaySpec with ScalaFutures {
  implicit val defaultpatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "CategoryController" must {

    "return a list of all categories"in new RepositoryAwareContext {
      new WithApplication(application) {
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category1 = categoryRepo.create(Category(None, "category1")).futureValue
        val category2 = categoryRepo.create(Category(None, "category2")).futureValue
        val category3 = categoryRepo.create(Category(None, "category3")).futureValue

        val catResponse = route(FakeRequest(GET,"/categories")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo))
          .get
        status(catResponse) must be(OK)
        contentType(catResponse) must be (Some("application/json"))
        val categories = contentAsJson(catResponse).as[Seq[Category]]
        categories.length mustBe 3
      }
    }

    "not allow non admins to create categoris"in new RepositoryAwareContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val newCategoryResponse = route(FakeRequest(POST, "/categories")
                      .withJsonBody(Json.obj("name"->"Test category"))
                      .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(newCategoryResponse) must be(FORBIDDEN)
      }
    }

    "allow admin to delete categories"in new RepositoryAwareContext {
      override val identity = User(Some(1), "The", "Admin", "admin@test.com", "credentials", "admin@test.com", Set("USER", "ADMINISTRATOR"))
      new WithApplication(application) {

        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

        val deleteCatString = "/categories/" + category.id.get
        val deleteCatResponse = route(FakeRequest(DELETE, deleteCatString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(deleteCatResponse) must be(OK)
        contentType(deleteCatResponse) mustBe Some("application/json")
      }
    }

    "not allow non admin to delete categories"in new RepositoryAwareContext {
      new WithApplication(application) {
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

        val deleteCatString = "/restaurants/" + category.id.get
        val deleteCatResponse = route(FakeRequest(DELETE, deleteCatString)
         .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(deleteCatResponse) must be(FORBIDDEN)
      }
    }

    /*"allow admin to create categories"in new RepositoryAwareContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val newCategoryResponse = route(FakeRequest(POST, "/categories")
                  .withJsonBody(Json.obj("name"->"Test category"))
                  .withAuthenticator[JWTAuthenticator](admin.loginInfo)
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(newCategoryResponse) must be(OK)
        contentType(newCategoryResponse) must be(Some("application/json"))
        val category = contentAsJson(newCategoryResponse).as[Category]
        category.id mustNot be (None)
        category.name mustBe "Test category"
      }
    }

    "find  restaurants by the category "in new RepositoryAwareContext {
      new WithApplication(application) {

      }
    }

    */

  }
}

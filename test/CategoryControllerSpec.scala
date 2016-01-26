import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._
import helpers.RepositoryAwareContext
import models.{Restaurant, User, Category}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.{RestaurantRepository, CategoryRepository}

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

    "find a restaurants by the category id"in new RepositoryAwareContext {
      new WithApplication(application) {

        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1", None, category.id.get, Some("+43 666 666 666"), Some("fun@coding.com"), None, None, "Alte Poststrasse", "Graz", "4020", 01.0101, 11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None, "Restaurant2", None, category.id.get, Some("+43 666 666 666"), Some("fun@coding.com"), None, None, "Alte Poststrasse", "Graz", "4020", 01.0101, 11.1001)).futureValue
        val restaurant3 = restaurantRepo.create(Restaurant(None, "Restaurant3", None, category.id.get, Some("+43 666 666 666"), Some("fun@coding.com"), None, None, "Alte Poststrasse", "Graz", "4020", 01.0101, 11.1001)).futureValue

        val findString = "/categories/" + category.id.get
        val findResponse = route(FakeRequest(GET, findString)).get

        status(findResponse) must be (OK)
        contentType(findResponse) must be(Some("application/json"))
        val restaurants = contentAsJson(findResponse).as[Seq[Restaurant]]
        val restaurantTest1 = restaurants.head
        val restaurantTest2 = restaurants.tail.head
        val restaurantTest3 = restaurants.tail.tail.head
        restaurants.length mustBe 3
        restaurantTest1 mustBe restaurant1
        restaurantTest2 mustBe restaurant2
        restaurantTest3 mustBe restaurant3

      }
    }

    "allow admin to create categories"in new RepositoryAwareContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = Category(None, "Italienisch")
        val newCategoryResponse = route(FakeRequest(POST, "/categories")

                  .withJsonBody(Json.toJson(category))
                  .withAuthenticator[JWTAuthenticator](admin.loginInfo)
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(newCategoryResponse) must be(OK)
        contentType(newCategoryResponse) must be(Some("application/json"))
        val categorytest = contentAsJson(newCategoryResponse).as[Category]
        categorytest.id mustNot be (None)
        categorytest.name mustBe "Italienisch"
      }
    }





  }
}

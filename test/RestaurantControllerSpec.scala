/**
 * Created by cemirrah13 on 19.01.2016.
 */

import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._
import helpers.RepositoryAwareContext
import models.{User, Category, Restaurant}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.{CategoryRepository, RestaurantRepository}


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class RestaurantControllerSpec extends PlaySpec with ScalaFutures{
  implicit val defaultpatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))



  "RestaurantController" must {

    "return a list with all restaurants " in new RepositoryAwareContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue

        val resReponse = route(FakeRequest(GET, "/restaurants")
              .withAuthenticator[JWTAuthenticator](identity.loginInfo))
          .get
        status(resReponse) must be(OK)
        contentType(resReponse) must be(Some("application/json"))
        val restaurants = contentAsJson(resReponse).as[Seq[Restaurant]]
        restaurants.length mustBe 2
        restaurants mustBe restaurants.sortBy(_.name)

      }
    }

    "get info about a specific restaurant by Id" in new RepositoryAwareContext {
       new WithApplication(application) {
         val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
         val categoryRepo = app.injector.instanceOf[CategoryRepository]
         val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
         val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
         val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue


         val restaurants = restaurantRepo.all().futureValue
         val restaurants2 = restaurants.tail.head
         val resResponse = route(FakeRequest(GET,s"/restaurants/${restaurants2.id.get}")
           .withAuthenticator[JWTAuthenticator](identity.loginInfo))
           .get
         status(resResponse) must be (OK)
         contentType(resResponse) must be (Some("application/json"))
         val restaurant = contentAsJson(resResponse).as[Restaurant]
         restaurant mustBe restaurant2


       }
      }

          "report an error if specific restaurant was not found" in new RepositoryAwareContext {
            new WithApplication(application) {
              val restaurantResponse = route(FakeRequest(GET, "/restaurants/99")
                  .withAuthenticator[JWTAuthenticator](identity.loginInfo))
                .get
              status(restaurantResponse) must be(NOT_FOUND)
              contentType(restaurantResponse) must be(Some("application/json"))
              (contentAsJson(restaurantResponse) \ "message").as[String] mustBe "Restaurant with id=99 was not found"

            }
          }

           "provide the number of restaurants" in new RepositoryAwareContext {
              new WithApplication(application) {
                val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
                val categoryRepo = app.injector.instanceOf[CategoryRepository]
                val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
                val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
                val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue

                val restaurantResponse = route(FakeRequest(GET, "/restaurants/count")
                  .withAuthenticator[JWTAuthenticator](identity.loginInfo))
                  .get
                status(restaurantResponse) must be(OK)
                contentType(restaurantResponse) must be(Some("application/json"))
                val count = contentAsJson(restaurantResponse).as[Int]
                count mustBe 2
              }
           }


    /*

           "allow admins to create new restaurants" in new RepositoryAwareContext {
             override val identity = User(Some(1), "The", "Admin", "admin@test.com", "credentials", "admin@test.com",Set("USER","ADMINISTRATOR"))
                  new WithApplication(application) {
                    val categoryRepo = app.injector.instanceOf[CategoryRepository]
                    val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

                    val token = CSRF.SignedTokenProvider.generateToken
                    val newRestaurantResponse = route(FakeRequest(POST, "/restaurants")
                      .withJsonBody(Json.obj("name"->"Test restaurants","description"->"Test Description", "category" ->"Italienisch","phone"->"+666 66 66 6","email"->"fun@max", "website"->"intenso.stark", "rating"->"2", "street"->"futja kot", "city"->"graz","zip"->"234", "lat"->"9.898", "lng"->"52.465"))

                      .withAuthenticator[JWTAuthenticator](identity.loginInfo)
                      .withHeaders("Csrf-Token" -> token)
                      .withSession("csrfToken"->token)
                    ).get
                    status(newRestaurantResponse) must be(OK)
                    contentType(newRestaurantResponse) must be(Some("application/json"))
                    val restaurant = contentAsJson(newRestaurantResponse).as[Restaurant]
                    restaurant.id mustNot be (None)
                    restaurant.name mustBe "Test restaurants"

                  }
           }

                         "allow admins to delete a restaurants" in new RepositoryAwareContext {
                           new WithApplication(application) {

                           }
                         }

                         "not allow non-admins to delete a restaurants" in new RepositoryAwareContext {
                           new WithApplication(application) {

                           }
                         }

                         "allow any user to paginate restaurants / full page" in new RepositoryAwareContext {
                           new WithApplication(application) {

                           }
                         }

                         "allow any user to paginate restaurants / incomplete page" in new RepositoryAwareContext {
                           new WithApplication(application) {

                           }
                         }*/
  }

}

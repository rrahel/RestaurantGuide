/**
 * Created by cemirrah13 on 19.01.2016.
 */

import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._
import helpers.RepositoryAwareContext
import models.Restaurant
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.RestaurantRepository


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class RestaurantControllerSpec extends PlaySpec with ScalaFutures{
  implicit val defaultpatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  def createTestRestaurants(number: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,"Albanisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))



  "RestaurantController" must {

    "return a list with all restaurants " in new RepositoryAwareContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        Future.sequence(createTestRestaurants(8).map(restaurantRepo.create)).futureValue
        val resReponse = route(FakeRequest(GET, "/restaurants")
              .withAuthenticator[JWTAuthenticator](identity.loginInfo))
          .get
        status(resReponse) must be(OK)
        contentType(resReponse) must be(Some("application/json"))
        val restaurants = contentAsJson(resReponse).as[Seq[Restaurant]]
        restaurants.length mustBe 8
        restaurants mustBe restaurants.sortBy(_.name)

      }
    }

    "get info about a specific restaurant by Id" in new RepositoryAwareContext {
       new WithApplication(application) {
         val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
         Future.sequence(createTestRestaurants(8).map(restaurantRepo.create)).futureValue
         val restaurants = restaurantRepo.all().futureValue
         val restaurants2 = restaurants.tail.head
         val resResponse = route(FakeRequest(GET,s"/restaurants/${restaurants2.id.get}")
           .withAuthenticator[JWTAuthenticator](identity.loginInfo))
           .get
         status(resResponse) must be (OK)
         contentType(resResponse) must be (Some("application/json"))
         val restaurant = contentAsJson(resResponse).as[Restaurant]
         restaurant mustBe restaurants2


       }
      }

      "report an error if specific restaurant was not found" in new RepositoryAwareContext {
        new WithApplication(application) {

        }
      }

    /*
         "provide the number of groups" in new RepositoryAwareContext {
           new WithApplication(application) {

           }
         }

         "allow admins to create new restaurants" in new RepositoryAwareContext {
           new WithApplication(application) {

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

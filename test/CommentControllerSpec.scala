/**
 * Created by Christoph on 19.01.2016.
 */
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import helpers.SecurityTestContext
import com.mohiva.play.silhouette.test._
import models.{Restaurant, Comment}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.{RestaurantRepository, CommentRepository}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class CommentControllerSpec extends PlaySpec with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(1, Seconds))
  import models.slick.DBPasswordInfo.dbTableElement2PasswordInfo

  "CommentController" must{

    "return a list with all comments from one restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val listAllString = "/comments/" + restaurant1.id.get
        val listAllReq = route(FakeRequest(GET, listAllString))
        .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get


      }


    }





  }




}

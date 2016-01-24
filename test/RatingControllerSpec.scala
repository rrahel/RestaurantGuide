/**
 * Created by Christoph on 22.01.2016.
 */
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import helpers.{RepositoryAwareContext, SecurityTestContext}
import com.mohiva.play.silhouette.test._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import scala.concurrent.ExecutionContext.Implicits.global
import repositories._
import models._

import scala.concurrent.Future
import org.scalatest.time.{Millis, Seconds, Span}

class RatingControllerSpec extends PlaySpec with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  def createTestRestaurants(number: Int, category: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,category,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))
  def createTestRestaurantsWithRating(number: Int, category: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,category,Some("+43 666 666 666"),Some("fun@coding.com"), None, Some(nr.toDouble), "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))

  "RatingController" must{

    "create rating for restaurant" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val createRatingResponse = route(FakeRequest(POST, "/rating")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(newRating))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(createRatingResponse) must be(OK)
        contentType(createRatingResponse) mustBe Some("application/json")
        val json = contentAsJson(createRatingResponse)
        val rating = Json.fromJson[Rating](json).get
        rating.id must not be(None)
        rating.rating must be(newRating.rating)

      }
    }

    "update rating for restaurant" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val createRatingResponse = route(FakeRequest(POST, "/rating")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(newRating))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(createRatingResponse) must be(OK)
        contentType(createRatingResponse) mustBe Some("application/json")
        val json = contentAsJson(createRatingResponse)
        val rating = Json.fromJson[Rating](json).get
        rating.id must not be(None)
        rating.rating must be(newRating.rating)

        val changeRating = Rating(rating.id, 10, insertedUser.id.get, restaurant1.id.get)
        val changeRatingString = "/rating/" + rating.id.get
        val updateRatingResponse = route(FakeRequest(POST, changeRatingString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(changeRating))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(updateRatingResponse) must be(OK)
        contentType(updateRatingResponse) mustBe Some("application/json")
        val json2 = contentAsJson(updateRatingResponse)
        val rating2 = Json.fromJson[Rating](json2).get
        rating2.id.get must be(rating.id.get)
        rating2.rating must be(changeRating.rating)
      }
    }

    "list top 6 restaurants from user" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val createdRestaurants = Future.sequence(createTestRestaurants(10,category.id.get).map(restaurantRepo.create)).futureValue
        createdRestaurants.size must be(10)
        val testRating1 = ratingRepo.save(Rating(None, 1, insertedUser.id.get,createdRestaurants.head.id.get),insertedUser.id.get).futureValue
        val testRating2 = ratingRepo.save(Rating(None, 2, insertedUser.id.get,createdRestaurants.tail.head.id.get),insertedUser.id.get).futureValue
        val testRating3 = ratingRepo.save(Rating(None, 3, insertedUser.id.get,createdRestaurants.tail.tail.head.id.get),insertedUser.id.get).futureValue
        val testRating4 = ratingRepo.save(Rating(None, 4, insertedUser.id.get,createdRestaurants.tail.tail.tail.head.id.get),insertedUser.id.get).futureValue
        val testRating5 = ratingRepo.save(Rating(None, 5, insertedUser.id.get,createdRestaurants.tail.tail.tail.tail.head.id.get),insertedUser.id.get).futureValue
        val testRating6 = ratingRepo.save(Rating(None, 6, insertedUser.id.get,createdRestaurants.tail.tail.tail.tail.tail.head.id.get),insertedUser.id.get).futureValue
        val testRating7 = ratingRepo.save(Rating(None, 7, insertedUser.id.get,createdRestaurants.tail.tail.tail.tail.tail.tail.head.id.get),insertedUser.id.get).futureValue
        val listTopResponse = route(FakeRequest(GET, "/rating/favorites")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(listTopResponse) must be(OK)
        contentType(listTopResponse) mustBe Some("application/json")
        val json = contentAsJson(listTopResponse)
        val ratingsSeq = Json.fromJson[List[Restaurant]](json).get
        ratingsSeq.length mustBe 6
        ratingsSeq.head.rating mustBe Some(testRating7.rating)
      }
    }

    "list top 6 restaurants" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        Future.sequence(createTestRestaurantsWithRating(10,category.id.get).map(restaurantRepo.create)).futureValue
        restaurantRepo.count().futureValue must be(10)
        val listTopResponse = route(FakeRequest(GET, "/rating/top")
          ).get
        status(listTopResponse) must be(OK)
        contentType(listTopResponse) mustBe Some("application/json")
        val json = contentAsJson(listTopResponse)
        val ratingsSeq = Json.fromJson[List[Restaurant]](json).get
        ratingsSeq.length mustBe 6
        ratingsSeq.head.rating mustBe Some(10.0)

      }
    }
  }

}

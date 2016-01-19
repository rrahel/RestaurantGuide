
/**
 * Created by Christoph on 16.01.2016.
 */

import models.{Restaurant, User, Rating}
import repositories.{RestaurantRepository, UserRepository, RatingRepository}

import helpers.SecurityTestContext
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import play.api.test.WithApplication
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeApplication
import org.scalatest._
import play.api.test._

import play.api.test.Helpers._
import org.scalatestplus.play._
import scala.concurrent.Future

import org.scalatest.time.{Millis, Seconds, Span}


class RatingRepositorySpec extends PlaySpec with ScalaFutures{

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "RatingRepository" must {
    "create new ratings" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating).futureValue
        testRating.id must not be(None)
        testRating.rating must be(newRating.rating)
      }
    }

    "read all ratings from one user" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating).futureValue
        val ratingsSeq = ratingRepo.readAllRatingsFromOneUser(insertedUser.id.get).futureValue
        ratingsSeq.size mustBe 1
        newRating.rating mustBe ratingsSeq.head.rating
      }
    }

    "read all ratings from one restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating).futureValue
        val ratingsSeq = ratingRepo.readAllRatingsFromOneRestaurant(restaurant1.id.get).futureValue
        ratingsSeq.size mustBe 1
        newRating.rating mustBe ratingsSeq.head.rating
      }
    }

    "update existing rating" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating).futureValue
        val changeRating = Rating(testRating.id, 10, insertedUser.id.get, restaurant1.id.get)
        val testChangeRating = ratingRepo.save(changeRating).futureValue
        testChangeRating.rating mustBe changeRating.rating
      }
    }

    "delete exisitng rating" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating).futureValue
        ratingRepo.delete(testRating.id.get)
        val existingRating = ratingRepo.find(testRating.id.get).futureValue
        existingRating must be(None)
      }
    }

  }

}



/**
 * Created by Christoph on 16.01.2016.
 */

import models.{Category, Restaurant, User, Rating}
import repositories.{CategoryRepository, RestaurantRepository, UserRepository, RatingRepository}

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

  def createTestRestaurants(number: Int, category: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,category,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))
  def createTestRestaurantsWithRating(number: Int, category: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,category,Some("+43 666 666 666"),Some("fun@coding.com"), None, Some(nr.toDouble), "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))

  "RatingRepository" must {
    "create new ratings" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating,insertedUser.id.get).futureValue
        val testUpdateRestaurant = restaurantRepo.find(restaurant1.id.get).futureValue
        testRating.id must not be(None)
        testRating.rating must be(newRating.rating)
        testUpdateRestaurant.get.rating must be(Some(newRating.rating))
      }
    }

    "read top 6 ratings from one user" in new SecurityTestContext {
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
        val topRatingSeq = ratingRepo.readTop6RatingsFromOneUser(insertedUser.id.get).futureValue
        topRatingSeq.size mustBe 6
        topRatingSeq.head.rating mustBe Some(7.0)
        topRatingSeq.tail.head.rating mustBe Some(6.0)
        topRatingSeq.tail.tail.tail.head.rating mustBe Some(4.0)
      }
    }

    "read top 6 restaurants" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        Future.sequence(createTestRestaurantsWithRating(10,category.id.get).map(restaurantRepo.create)).futureValue
        restaurantRepo.count().futureValue must be(10)
        val topRestaurantSeq = ratingRepo.readTop6Ratings().futureValue
        topRestaurantSeq.size must be(6)
        topRestaurantSeq.head.rating must be(Some(10.0))
        topRestaurantSeq.tail.head.rating must be(Some(9.0))
      }
    }

    "read rating from one restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating,insertedUser.id.get).futureValue
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
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating = Rating(None, 5, insertedUser.id.get, restaurant1.id.get)
        val testRating = ratingRepo.save(newRating,insertedUser.id.get).futureValue
        val changeRating = Rating(testRating.id, 10, insertedUser.id.get, restaurant1.id.get)
        val testChangeRating = ratingRepo.save(changeRating,insertedUser.id.get).futureValue
        val testUpdateRestaurant = restaurantRepo.find(restaurant1.id.get).futureValue
        testChangeRating.rating mustBe changeRating.rating
        testUpdateRestaurant.get.rating must be(Some(testChangeRating.rating))
      }
    }

    "delete all ratings from one user" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser1 = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val insertedUser2 = userRepo.save(User(None, "Jane", "Miller", "jm@test.com", "test2", "test2")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None, "Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant3 = restaurantRepo.create(Restaurant(None, "Restaurant3",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating1 = ratingRepo.save(Rating(None, 5, insertedUser1.id.get, restaurant1.id.get),insertedUser1.id.get).futureValue
        val newRating2 = ratingRepo.save(Rating(None, 5, insertedUser1.id.get, restaurant2.id.get),insertedUser1.id.get).futureValue
        val newRating3 = ratingRepo.save(Rating(None, 5, insertedUser2.id.get, restaurant3.id.get),insertedUser1.id.get).futureValue
        val deleteRating = ratingRepo.deleteRatingFromUser(insertedUser1.id.get).futureValue
        val readRating1 = ratingRepo.readTop6RatingsFromOneUser(insertedUser1.id.get).futureValue
        readRating1.size mustBe 0
        val readRating2 = ratingRepo.readTop6RatingsFromOneUser(insertedUser2.id.get).futureValue
        readRating2.size mustBe 1
      }
    }

    "update rating from a restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val ratingRepo = app.injector.instanceOf[RatingRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val insertedUser1 = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val insertedUser2 = userRepo.save(User(None, "Jane", "Miller", "jm@test.com", "test2", "test2")).futureValue
        val restaurant = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newRating1 = ratingRepo.save(Rating(None, 5, insertedUser1.id.get, restaurant.id.get),insertedUser1.id.get).futureValue
        val newRating2 = ratingRepo.save(Rating(None, 10, insertedUser1.id.get, restaurant.id.get),insertedUser1.id.get).futureValue
        val calcRating = ratingRepo.calcRatingForRestaurant(restaurant.id.get).futureValue
        val updateRestaurant = ratingRepo.updateRatingOfRestaurant(restaurant, calcRating).futureValue
        val foundRestaurant = restaurantRepo.find(restaurant.id.get).futureValue
        foundRestaurant.get.rating mustBe Some(7.5)
      }
    }

  }

}


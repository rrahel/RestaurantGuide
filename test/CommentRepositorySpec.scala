/**
 * Created by Christoph on 16.01.2016.
 */

import models.{Restaurant, User, Comment}
import repositories.{RestaurantRepository, UserRepository, CommentRepository}

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

class CommentRepositorySpec extends PlaySpec with ScalaFutures{

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))



  "CommentRepository" must {
    "create new comments" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        testComment.id must not be(None)
        testComment.content must be(newComment.content)
      }
    }

    "read all comments from one user" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        val commentsSeq = commentRepo.readOneCommentFromOneUser(newComment.id.get, insertedUser.id.get).futureValue
        newComment.content mustBe commentsSeq.head.content
      }
    }

    "read all comments from one restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        val commentsSeq = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get,0,1).futureValue
        newComment.content mustBe commentsSeq.head.content
      }
    }

    "support pagination when listing comments " in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment1", insertedUser.id.get, restaurant1.id.get)
        val newComment2 = Comment(None, "testComment2", insertedUser.id.get, restaurant1.id.get)
        val newComment3 = Comment(None, "testComment3", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        val testComment2 = commentRepo.save(newComment2).futureValue
        val testComment3 = commentRepo.save(newComment3).futureValue
        val page1 = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get, 0, 2).futureValue
        page1.length mustBe 2
        page1.head.content mustBe "testComment1"
        page1(1).content mustBe "testComment2"
        val page2 = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get, 1, 2).futureValue
        page2.length mustBe 1
        page2.head.content mustBe "testComment3"
      }
    }

    "update existing comment" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        val changeComment = Comment(testComment.id, "testChangeComment", insertedUser.id.get, restaurant1.id.get)
        val testChangeComment = commentRepo.save(changeComment).futureValue
      }
    }

    "delete exisitng comment" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,"Italienisch",Some("+43 666 666 666"),Some("fun@coding.com"), None, None, None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment).futureValue
        commentRepo.delete(testComment.id.get)
        val existingComment = commentRepo.find(testComment.id.get).futureValue
        existingComment must be(None)
      }
    }

  }

}


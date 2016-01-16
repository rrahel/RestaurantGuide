/**
 * Created by Christoph on 16.01.2016.
 */

import models.{User, Comment}
import repositories.{UserRepository, CommentRepository}

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

class CommentRepositorySpec extends PlaySpec with ScalaFutures{


  "CommentRepository" must {
    "create new comments" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, 1)
        val testComment = commentRepo.save(newComment).futureValue
        //val commentsSeq = commentRepo.readAllCommentsFromOneUser(insertedUser.id.get).futureValue
        //newComment.content mustBe commentsSeq.head.content
        testComment must not be(None)
        testComment must be(newComment.content)
      }
    }

    "read all comments from one user" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, 1)
        val testComment = commentRepo.save(newComment).futureValue
        val commentsSeq = commentRepo.readAllCommentsFromOneUser(insertedUser.id.get).futureValue
        newComment.content mustBe commentsSeq.head.content
      }
    }

    "read all comments from one restaurant" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, 1)
        val testComment = commentRepo.save(newComment).futureValue
        val commentsSeq = commentRepo.readAllCommentsFromOneRestaurant(1).futureValue
        newComment.content mustBe commentsSeq.head.content
      }
    }

    "update existing comment" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, 1)
        val testComment = commentRepo.save(newComment).futureValue
        val changeComment = Comment(testComment.id, "testChangeComment", insertedUser.id.get, 1)
        val testChangeComment = commentRepo.save(changeComment).futureValue
      }
    }

    "delete exisitng comment" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", None, "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, 1)
        val testComment = commentRepo.save(newComment).futureValue
        commentRepo.delete(testComment.id.get)
        val existingComment = commentRepo.find(testComment.id.get)
        existingComment must be(None)
      }
    }

  }

}

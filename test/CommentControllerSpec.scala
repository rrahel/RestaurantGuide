/**
 * Created by Christoph on 19.01.2016.
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
import repositories.{CategoryRepository, UserRepository, CommentRepository, RestaurantRepository}
import models.{Category, User, Comment, Restaurant}

import scala.concurrent.Future
import org.scalatest.time.{Millis, Seconds, Span}

class CommentControllerSpec extends PlaySpec with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  "CommentController" must{

    "return a list with all comments from one restaurant" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None, "Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val newComment2 = Comment(None, "testComment", insertedUser.id.get, restaurant2.id.get)
        val testComment = commentRepo.save(newComment,insertedUser.id.get).futureValue
        val testComment2 = commentRepo.save(newComment2,insertedUser.id.get).futureValue
        val listAllString = "/comments/" + restaurant1.id.get
        val listAllResponse = route(FakeRequest(GET, listAllString)
         ).get
        status(listAllResponse) must be(OK)
        contentType(listAllResponse) mustBe Some("application/json")
        val json = contentAsJson(listAllResponse)
        val comments = Json.fromJson[List[Comment]](json).get
        comments.length mustBe 1
        comments.head.content mustBe testComment.content
      }
    }

    "return a list with all comments from the logged in user" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None, "Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val insertedUser2 = userRepo.save(User(None, "Jane", "Miller", "jm@test.com", "test2", "test2")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val newComment2 = Comment(None, "testComment2", insertedUser2.id.get, restaurant2.id.get)
        val testComment = commentRepo.save(newComment,insertedUser.id.get).futureValue
        val testComment2 = commentRepo.save(newComment2, insertedUser2.id.get).futureValue
        val listAllResponse = route(FakeRequest(GET, "/comments")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(listAllResponse) must be(OK)
        contentType(listAllResponse) mustBe Some("application/json")
        val json = contentAsJson(listAllResponse)
        val comments = Json.fromJson[List[Comment]](json).get
        comments.length mustBe 1
        comments.head.content mustBe testComment.content
      }
    }

    "allow admins to delete comments" in new RepositoryAwareContext {
      override val identity = User(Some(1), "The", "Admin", "admin@test.com", "credentials", "admin@test.com",Set("USER","ADMINISTRATOR"))
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment,insertedUser.id.get).futureValue
        val deleteCommentString = "/commentA/" + testComment.id.get
        val deleteCommentResponse = route(FakeRequest(DELETE, deleteCommentString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(deleteCommentResponse) must be(OK)
        contentType(deleteCommentResponse) mustBe Some("application/json")

      }
    }

    "user cannot delete all comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment,insertedUser.id.get).futureValue
        val deleteCommentString = "/commentA/" + testComment.id.get
        val deleteCommentResponse = route(FakeRequest(DELETE, deleteCommentString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(deleteCommentResponse) must be(FORBIDDEN)

      }
    }

    "user can delete their own comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val testComment = commentRepo.save(newComment,insertedUser.id.get).futureValue
        val deleteCommentString = "/comment/" + testComment.id.get
        val deleteCommentResponse = route(FakeRequest(DELETE, deleteCommentString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(deleteCommentResponse) must be(OK)
        contentType(deleteCommentResponse) mustBe Some("application/json")
        val comment = commentRepo.find(testComment.id.get).futureValue
        comment mustBe None
      }
    }

    "user can create comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val createCommentResponse = route(FakeRequest(POST, "/comment")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(newComment))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(createCommentResponse) must be(OK)
        contentType(createCommentResponse) mustBe Some("application/json")
        val foundComment = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get,0,5).futureValue
        foundComment.head.content must be("testComment")
      }
    }

    "user cannot delete other comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue

        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val createCommentResponse = route(FakeRequest(POST, "/comment")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(newComment))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(createCommentResponse) must be(OK)

        val testComment = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get,0,5).futureValue
        val token2 = CSRF.SignedTokenProvider.generateToken
        val deleteCommentString = "/comment/" + testComment.head.id.get
        val deleteCommentResponse = route(FakeRequest(DELETE, deleteCommentString)
          .withAuthenticator[JWTAuthenticator](identity2.loginInfo)
        ).get
        status(deleteCommentResponse) must be(BAD_REQUEST)

      }
    }

    "user can update own comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val testComment = commentRepo.save(Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get),insertedUser.id.get).futureValue
        val changeComment = Comment(testComment.id, "changeComment", insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val changeCommentString = "/comment/" + testComment.id.get
        val changeCommentResponse = route(FakeRequest(POST, changeCommentString)
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(changeComment))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(changeCommentResponse) must be(OK)
        contentType(changeCommentResponse) mustBe Some("application/json")
        val comment = contentAsJson(changeCommentResponse).as[Comment]
        comment.content mustBe changeComment.content
        val foundComments = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get,0,5).futureValue
        foundComments.length mustBe 1
      }
    }

    "user cannot update other comments" in new RepositoryAwareContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue

        val newComment = Comment(None, "testComment", insertedUser.id.get, restaurant1.id.get)
        val token = CSRF.SignedTokenProvider.generateToken
        val createCommentResponse = route(FakeRequest(POST, "/comment")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)
          .withJsonBody(Json.toJson(newComment))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
        ).get
        status(createCommentResponse) must be(OK)

        val testComment = commentRepo.readAllCommentsFromOneRestaurant(restaurant1.id.get,0,5).futureValue
        val token2 = CSRF.SignedTokenProvider.generateToken
        val changeComment = Comment(None, "changeComment", insertedUser.id.get, restaurant1.id.get)
        val updateCommentString = "/comment/" + testComment.head.id.get
        val updateCommentResponse = route(FakeRequest(POST, updateCommentString)
          .withAuthenticator[JWTAuthenticator](identity2.loginInfo)
          .withJsonBody(Json.toJson(changeComment))
          .withHeaders("Csrf-Token" -> token2)
          .withSession("csrfToken"->token2)
        ).get
        status(updateCommentResponse) must be(BAD_REQUEST)

      }
    }




  }




}

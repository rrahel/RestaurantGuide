/**
 * Created by cemirrah13 on 19.01.2016.
 */
import helpers.SecurityTestContext
import models.{User, Comment, Restaurant, Category}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import play.api.test.WithApplication
import repositories.{CategoryRepository, UserRepository, CommentRepository, RestaurantRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest.time.{Millis, Seconds, Span}

class RestaurantRepoSpec extends PlaySpec with ScalaFutures{

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  //def createTestCategory = Category(None,"test")
  //def createTestRestaurants(number: Int) = (1 to number).map(nr => Restaurant(None, s"Restaurant$nr",None,createTestCategory.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001 ))

  "RestaurantRepo" must{

    "get the number of restaurants " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue

        restaurantRepo.count().futureValue mustBe 2
      }
    }

    "find a restaurant with its id " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant = restaurantRepo.find(restaurant1.id.get).futureValue.get
        restaurant mustBe restaurant1
      }
    }

    "create new restaurants " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val newRestaurant = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val italienisch = restaurantRepo.create(newRestaurant).futureValue
        italienisch.id mustNot be(None)
        italienisch.name mustBe newRestaurant.name
        italienisch.category mustBe newRestaurant.category
      }
    }

    "list all restaurants " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val emptyList = restaurantRepo.all().futureValue
        emptyList.length mustBe 0
        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        restaurantRepo.create(restaurant1).futureValue
        restaurantRepo.create(restaurant2).futureValue
        val restaurants = restaurantRepo.all().futureValue
        restaurants.length mustBe 2
        restaurants(0).name mustBe "Restaurant1"
        restaurants(1).name mustBe "Restaurant2"

      }
    }


    "support pagination when listing restaurants " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant3 = restaurantRepo.create(Restaurant(None, "Restaurant3",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant4 = restaurantRepo.create(Restaurant(None,"Restaurant4",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant5 = restaurantRepo.create(Restaurant(None, "Restaurant5",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant6 = restaurantRepo.create(Restaurant(None,"Restaurant6",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant7 = restaurantRepo.create(Restaurant(None, "Restaurant7",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant8 = restaurantRepo.create(Restaurant(None,"Restaurant8",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue

        val page1 = restaurantRepo.all(0,5).futureValue
        page1.length mustBe 5
        page1.head.name mustBe "Restaurant1"
        page1(3).name mustBe "Restaurant4"
        val page2 = restaurantRepo.all(1,5).futureValue
        page2.length mustBe 3
        page2.head.name mustBe "Restaurant6"
        page2(2).name mustBe "Restaurant8"


      }
    }

    "support deleting restaurants, even when they have comments " in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
        val commentRepo = app.injector.instanceOf[CommentRepository]
        val userRepo = app.injector.instanceOf[UserRepository]

        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val user1 = userRepo.save(User(None,"Jane","Miller","jm@test.com", "test","test")).futureValue
        val comment1 = commentRepo.save(Comment(None, "Super geil", user1.id.get,restaurant1.id.get ), user1.id.get)
        val comment2 = commentRepo.save(Comment(None, "Super cool", user1.id.get,restaurant1.id.get ), user1.id.get)

        restaurantRepo.all().futureValue.length mustBe 2
        restaurantRepo.delete(1).futureValue
        restaurantRepo.all().futureValue.length mustBe 1
        restaurantRepo.find(1).futureValue mustBe None

      }
    }

    "find restaurants by name" in new SecurityTestContext {
      new WithApplication(application) {
        val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
        val categoryRepo = app.injector.instanceOf[CategoryRepository]
        val category = categoryRepo.create(Category(None, "Italienisch")).futureValue

        val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant2 = restaurantRepo.create(Restaurant(None,"Restaurant2",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
        val restaurant3 = restaurantRepo.create(Restaurant(None, "Restaurant3",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue

        val restaurants = restaurantRepo.findResByName(restaurant1.name).futureValue
        restaurants.length mustBe 1
        val restaurants1 = restaurantRepo.findResByName("Rest").futureValue
        restaurants1.length mustBe 3
      }
    }

  }

}

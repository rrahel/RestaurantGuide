import helpers.SecurityTestContext
import models.{Restaurant, Category}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import play.api.test.WithApplication
import repositories.{RestaurantRepository, CategoryRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.scalatest.time.{Millis, Seconds, Span}


/**
 * Created by cemirrah13 on 22.01.2016.
 */
class CategoryRepoSpec extends PlaySpec with ScalaFutures{

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
 "CategoryRepo" must{

  "create new category" in new SecurityTestContext {
    new WithApplication(application) {
      val categoryRepo = app.injector.instanceOf[CategoryRepository]
      val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
      category.id mustNot be(None)
      category.name mustBe category.name


    }
  }

 "find category" in new SecurityTestContext {
   new WithApplication(application) {
     val categoryRepo = app.injector.instanceOf[CategoryRepository]
     val restaurantRepo = app.injector.instanceOf[RestaurantRepository]
     val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
     val category1 = categoryRepo.create(Category(None, "Deutch")).futureValue
     val restaurant1 = restaurantRepo.create(Restaurant(None, "Restaurant1",None,category.id.get,Some("+43 666 666 666"),Some("fun@coding.com"), None, None, "Alte Poststrasse","Graz","4020",01.0101,11.1001)).futureValue
     val cat = categoryRepo.find(category.id.get).futureValue
     cat.size mustBe 1
   }
 }

   "delete category" in new SecurityTestContext {
     new WithApplication(application) {
     val categoryRepo = app.injector.instanceOf[CategoryRepository]
     val category = categoryRepo.create(Category(None, "Italienisch")).futureValue
     val category1 = categoryRepo.create(Category(None, "Deutch")).futureValue

       categoryRepo.all().futureValue.length mustBe 2
       categoryRepo.delete(1).futureValue
       categoryRepo.all().futureValue.length mustBe 1

    }
   }

   "list all categories" in new SecurityTestContext {
     new WithApplication(application) {
       val categoryRepo = app.injector.instanceOf[CategoryRepository]
       categoryRepo.all().futureValue.length mustBe 0
       val category = categoryRepo.create(Category(None, "Category1")).futureValue
       val category1 = categoryRepo.create(Category(None, "Category2")).futureValue

       val categories = categoryRepo.all().futureValue
       categories.length mustBe 2
       categories(0).name mustBe "Category1"
       categories(1).name mustBe "Category2"

     }
   }
}
}

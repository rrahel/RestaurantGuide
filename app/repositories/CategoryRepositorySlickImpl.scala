package repositories

import models.{Restaurant, Category}
import models.slick.{Restaurants, Categories}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.H2Driver.api._

import scala.concurrent.Future

/**
 * Created by Christoph on 21.01.2016.
 */
class CategoryRepositorySlickImpl extends CategoryRepository with HasDatabaseConfig[JdbcProfile]{

  override protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val categories = TableQuery[Categories]
  val restaurants = TableQuery[Restaurants]

  private val allQuery = categories.sortBy(r => (r.name.asc))

  /**
   * create a new category
   * @param category
   * @return
   */


  override def create(category: Category): Future[Category] = {
    val existingCategoryFuture = category.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }

    existingCategoryFuture.flatMap{
      case None => db.run(
        (categories returning categories.map(_.id)
          into((restaurant, id) => restaurant.copy(id = Some(id))))+= category
      )
      case Some(_) => db.run(
      for{
        updateCategory <- categories.filter(_.id === category.id).update(category)
      }yield category
      )
    }
  }

  override def allByCategory(categoryId: Int): Future[Seq[Restaurant]] =
    db.run(restaurants.filter(_.category === categoryId).result)

  override def find(categoryId: Int): Future[Seq[Restaurant]] =
    db.run(restaurants.filter(_.category === categoryId).result)


 override def delete(categoryId: Int): Future[Unit] = {
    val delQuery = for{
      categoryDel <- categories.filter(_.id === categoryId).delete
    }yield (categoryDel)
    db.run(delQuery).map(_ => {})
  }

  override def all(): Future[Seq[Category]] = db.run(allQuery.result)

  override def count(categoryId: Int): Future[Int] = {
    db.run(restaurants.filter(_.category === categoryId).length.result)
  }
}

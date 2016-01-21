package repositories

import models.Category
import models.slick.Categories
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

  /**
   * create a new category
   * @param category
   * @return
   */
  override def create(category: Category): Future[Category] = {
    db.run(categories returning categories.map(_.id) into ((category,id) => category.copy(id = Some(id)))+=category)
  }
}

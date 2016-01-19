package repositories

/**
 * Created by cemirrah13 on 19.01.2016.
 */

import models.Restaurant
import models.slick.Restaurants
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
//import slick.driver.PostgresDriver.api._
import slick.driver.H2Driver.api._
import scala.concurrent.Future

class RestaurantRepositoryImpl extends RestaurantRepository with HasDatabaseConfig[JdbcProfile]{
  //get the number of all restaurants
  override def count(): Future[Int] = db.run(restaurants.length.result)

  //list all restaurants
  override def all(): Future[Seq[Restaurant]] = db.run(allQuery.result)

  //list all restaurants in a pagination fashion
  override def all(page: Int, pageSize: Int): Future[Seq[Restaurant]] = db.run(
    allQuery.drop(page * pageSize).take(pageSize).result)

  //delete a restaurant
  override def delete(restaurantId: Int): Future[Unit] = {

  }

  //find a particular restaurant
  override def find(restaurantId: Int): Future[Option[Restaurant]] = ???

  //create a restaurant
  override def create(restaurant: Restaurant): Future[Restaurant] = ???

  override protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
    val restaurants = TableQuery[Restaurants]
    private val allQuery = restaurants.sortBy(r => (r.name.asc))
}

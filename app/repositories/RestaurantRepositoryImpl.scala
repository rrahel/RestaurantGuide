package repositories

/**
 * Created by cemirrah13 on 19.01.2016.
 */

import models.Restaurant
import models.slick.{Comments, Restaurants}
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
    val delQuery = for{
      //we must delete the ratins **** dont forget *****
      restaurantCommentsDel <- comments.filter(_.restaurantId === restaurantId).delete
      restaurantDel <- restaurants.filter(_.id === restaurantId).delete
    }yield (restaurantCommentsDel, restaurantDel)
    db.run(delQuery).map(_ => {})
  }

  //find a particular restaurant
  override def find(restaurantId: Int): Future[Option[Restaurant]] = db.run(
    restaurants.filter(_.id === restaurantId).result.headOption)

  //create a restaurant
  override def create(restaurant: Restaurant): Future[Restaurant] = {
    val existingRestaurantFuture = restaurant.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingRestaurantFuture.flatMap{
      case None => db.run(
        (restaurants returning restaurants.map(_.id)
          into((restaurant, id)=> restaurant.copy(id = Some(id)))) += restaurant
      )
      case Some(_) => db.run(
      for{
        updateRestaurant <- restaurants.filter(_.id === restaurant.id).update(restaurant)
      }yield restaurant
      )

    }
  }

  //find restaurants by the category
  override def findResByCat(catId: Int): Future[Some[Restaurant]] = ???




  override protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  val restaurants = TableQuery[Restaurants]
  val comments = TableQuery[Comments]
  private val allQuery = restaurants.sortBy(r => (r.name.asc))

}

package repositories

/**
 * Created by cemirrah13 on 19.01.2016.
 */

import models.Restaurant

import scala.concurrent.Future

trait RestaurantRepository {

  //get the number of all restaurants
  def count():Future[Int]

  //find a particular restaurant by id
  def find(restaurantId: Int):Future[Option[Restaurant]]

  //create a restaurant
  def create(restaurant: Restaurant):Future[Restaurant]

  //list all restaurants
  def all():Future[Seq[Restaurant]]

  //list all restaurants in a pagination fashion
  def all(page:Int, pageSize:Int):Future[Seq[Restaurant]]

  //delete a restaurant
  def delete(restaurantId:Int):Future[Unit]
}

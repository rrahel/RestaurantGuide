package repositories

import models.Rating

import scala.concurrent.Future

/**
 * Created by Christoph on 16.01.2016.
 */
trait RatingRepository {

  /**
   * create/update a rating
   * @param rating
   * @return
   */
  def save(rating: Rating): Future[Rating]

  /**
   * find all ratings from one restaurant
   * @param restaurantId
   * @return
   */
  def readAllRatingsFromOneRestaurant(restaurantId: Int):Future[Seq[Rating]]

  /**
   * find all ratings from one user
   * @param userId
   * @return
   */
  def readAllRatingsFromOneUser(userId: Int):Future[Seq[Rating]]

  /**
   * delete an existing rating
   * @param ratingId
   * @return
   */
  def delete(ratingId: Int):Future[Unit]

  /**
   * find existing rating by id
   * @param ratingId
   * @return
   */
  def find(ratingId: Int):Future[Option[Rating]]

}

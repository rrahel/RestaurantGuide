package repositories

import models.{Restaurant, Rating}

import scala.concurrent.Future

/**
 * Created by Christoph on 16.01.2016.
 */
trait RatingRepository {

  /**
   * create/update a rating
   * @param rating
   * @param userId
   * @return
   */
  def save(rating: Rating, userId: Int): Future[Rating]

  /**
   * find all ratings from one restaurant
   * @param restaurantId
   * @return
   */
  def readAllRatingsFromOneRestaurant(restaurantId: Int):Future[Seq[Rating]]

  /**
   * find top 6 restaurants from one user
   * @param userId
   * @return
   */
  def readTop6RatingsFromOneUser(userId: Int):Future[Seq[Restaurant]]

  /**
   * find top 6 restaurants
   * @return
   */
  def readTop6Ratings():Future[Seq[Restaurant]]

  /**
   * find one rating from one user
   * @param ratingId
   * @param userId
   * @return
   */
  def readOneRatingsFromOneUser(ratingId: Int, userId: Int):Future[Option[Rating]]

  /** *
    * delete all ratings from one user
    * @param userId
    * @return
    */
  def deleteRatingFromUser(userId: Int):Future[Unit]

  /**
   * find existing rating by id
   * @param ratingId
   * @return
   */
  def find(ratingId: Int):Future[Option[Rating]]

  /**
   * calculate the rating of a restaurant
   * @param restaurantID
   * @return
   */
  def calcRatingForRestaurant(restaurantID: Int):Future[Double]

  /**
   * update the rating of a restaurant
   * @param restaurant
   * @param rating
   */
  def updateRatingOfRestaurant(restaurant: Restaurant, rating: Double):Future[Restaurant]

  /**
   * find existing rating by user and restaurant id
   * @param userId
   * @param restaurantId
   * @return
   */
  def findSpecific(userId: Int, restaurantId: Int): Future[Option[Rating]]


}

package repositories

import models.{Rating, Restaurant}
import models.slick.{Restaurants, Ratings}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.H2Driver.api._

import scala.concurrent.Future

/**
 * Created by Christoph on 19.01.2016.
 */
class RatingRepositorySlickImpl extends RatingRepository with HasDatabaseConfig[JdbcProfile]{

  override protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val ratings = TableQuery[Ratings]
  private val restaurants = TableQuery[Restaurants]

  /**
   * create/update a rating
   * @param rating
   * @return
   */
  override def save(rating: Rating, userId: Int): Future[Rating] = {
    val existingRatingFuture = rating.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingRatingFuture.flatMap {
      case None => db.run(
        for {
        rate <- ratings returning ratings.map(_.id) into ((rating,id) => rating.copy(id = Some(id),userId = userId))+=rating
        updateRest <- (restaurants.filter(_.id === rating.restaurantId).result.headOption).map(
          restaurant => calcRatingForRestaurant(rating.restaurantId).map(
            newRating => updateRatingOfRestaurant(restaurant.get, newRating)
          )
        )
        } yield rate )

      case Some(_) => db.run(
        for {
          updateRating <- ratings.filter(_.id === rating.id).update(rating)
          updateRest <- (restaurants.filter(_.id === rating.restaurantId).result.headOption).map(
            restaurant => calcRatingForRestaurant(rating.restaurantId).map(
              newRating => updateRatingOfRestaurant(restaurant.get, newRating)
            )
        )
        } yield rating
      )
    }
  }

  /**
   * find all ratings from one restaurant
   * @param restaurantId
   * @return
   */
  override def readAllRatingsFromOneRestaurant(restaurantId: Int): Future[Seq[Rating]] = {
    db.run(ratings.filter(_.restaurantId === restaurantId).result)
  }

  /**
   * find top 6 restaurants from one user
   * @param userId
   * @return
   */
  override def readTop6RatingsFromOneUser(userId: Int): Future[Seq[Restaurant]] = {
    db.run((for {
      foundRatings <- ratings.filter(_.userId === userId)
      foundRestaurants <- restaurants.filter(_.id === foundRatings.restaurantId)
    } yield foundRestaurants).sortBy(_.rating.desc).take(6).result)
  }

  /**
   * find top 6 restaurants
   * @return
   */
  override def readTop6Ratings(): Future[Seq[Restaurant]] = {
    db.run(restaurants.sortBy(_.rating.desc).take(6).result)
  }

  /**
   * find existing rating by id
   * @param ratingId
   * @return
   */
  override def find(ratingId: Int): Future[Option[Rating]] = {
    db.run(ratings.filter(_.id === ratingId).result.headOption)
  }

  /**
   * delete all ratings from one user
   * @param userId
   * @return
    */
  override def deleteRatingFromUser(userId: Int): Future[Unit] = {
   db.run(ratings.filter(_.userId === userId).delete).map(_ => {})
  }

  /**
   * calculate the rating of a restaurant
   * @param restaurantID
   * @return
   */
  override def calcRatingForRestaurant(restaurantID: Int): Future[Double] = {
    db.run(ratings.filter(_.restaurantId === restaurantID).result).map(
      listRatings => (0.0 /: listRatings){_ + _.rating} / listRatings.length
    )
  }

  /**
   * update the rating of a restaurant
   * @param restaurant
   * @param rating
   */
  override def updateRatingOfRestaurant(restaurant: Restaurant, rating: Double): Future[Restaurant] = {
    val updateRestaurant:Restaurant = new Restaurant(restaurant.id, restaurant.name, restaurant.description, restaurant.category, restaurant.phone, restaurant.email, restaurant.website, Some(rating), restaurant.street, restaurant.city, restaurant.zip, restaurant.lat, restaurant.lng)
    db.run(for {
      updateRating <- restaurants.filter(_.id === restaurant.id).update(updateRestaurant)
    } yield restaurant )

  }

  /**
   * find one rating from one user
   * @param ratingId
   * @param userId
   * @return
   */
  override def readOneRatingsFromOneUser(ratingId: Int, userId: Int): Future[Option[Rating]] = {
    db.run(ratings.filter(x => (x.id === ratingId && x.userId === userId)).result.headOption)
  }


}

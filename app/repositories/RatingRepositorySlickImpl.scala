package repositories

import models.Rating
import models.slick.Ratings
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

  /**
   * create/update a rating
   * @param rating
   * @return
   */
  override def save(rating: Rating): Future[Rating] = {
    val existingRatingFuture = rating.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingRatingFuture.flatMap {
      case None =>
        db.run(ratings returning ratings.map(_.id) into ((rating,id) => rating.copy(id = Some(id)))+=rating)
      case Some(_) => db.run(
        for {
          updateRating <- ratings.filter(_.id === rating.id).update(rating)
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
   * find all ratings from one user
   * @param userId
   * @return
   */
  override def readAllRatingsFromOneUser(userId: Int): Future[Seq[Rating]] = {
    db.run(ratings.filter(_.userId === userId).result)
  }

  /**
   * delete an existing rating
   * @param ratingId
   * @return
   */
  override def delete(ratingId: Int): Future[Unit] = {
    db.run(ratings.filter(_.id === ratingId).delete).map(_ => {})
  }


  /**
   * find existing rating by id
   * @param ratingId
   * @return
   */
  override def find(ratingId: Int): Future[Option[Rating]] = {
    db.run(ratings.filter(_.id === ratingId).result.headOption)
  }
}

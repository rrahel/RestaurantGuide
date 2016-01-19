package repositories

import models.Comment
import models.slick.Comments
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.H2Driver.api._

import scala.concurrent.Future

/**
 * Created by Christoph on 16.01.2016.
 */
class CommentRepositorySlickImpl extends CommentRepository with HasDatabaseConfig[JdbcProfile]{

  override protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val comments = TableQuery[Comments]

  /**
   * create/update a comment
   * @param comment
   * @return
   */
  override def save(comment: Comment): Future[Comment] = {
    val existingCommentFuture = comment.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingCommentFuture.flatMap {
      case None =>
        db.run(comments returning comments.map(_.id) into ((comment,id) => comment.copy(id = Some(id)))+=comment)
      case Some(_) => db.run(
          for {
            updateComment <- comments.filter(_.id === comment.id).update(comment)
          } yield comment
        )
    }
  }

  /**
   * find all comments from one restaurant
   * @param restaurantId
   * @return
   */
  override def readAllCommentsFromOneRestaurant(restaurantId: Int, page: Int, pageSize: Int): Future[Seq[Comment]] = {
    db.run((comments.filter(_.restaurantId === restaurantId)).drop(page * pageSize).take(pageSize).result)
  }

  /**
   * find all comments from one user
   * @param userId
   * @return
   */
  override def readOneCommentFromOneUser(commentId: Int, userId: Int): Future[Option[Comment]] = {
    db.run(comments.filter(x => (x.id === commentId && x.userId === userId)).result.headOption)
  }

  /**
   * delete an existing comment
   * @param commentId
   * @return
   */
  override def delete(commentId: Int): Future[Unit] = {
    db.run(comments.filter(_.id === commentId).delete).map(_ => {})
  }

  /**
   * find existing comment by id
   * @param commentId
   * @return
   */
  override def find(commentId: Int): Future[Option[Comment]] = {
    db.run(comments.filter(_.id === commentId).result.headOption)
  }
}

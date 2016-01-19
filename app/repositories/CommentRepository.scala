package repositories

import models.Comment

import scala.concurrent.Future

/**
 * Created by Christoph on 16.01.2016.
 */
trait CommentRepository {

  /**
   * create/update a comment
   * @param comment
   * @return
   */
  def save(comment: Comment, userId: Int): Future[Comment]

  /**
   * find all comments from one restaurant
   * @param restaurantId
   * @param page
   * @param pageSize
   * @return
   */
  def readAllCommentsFromOneRestaurant(restaurantId: Int, page: Int, pageSize: Int):Future[Seq[Comment]]

  /**
   * find all comments from one user
   * @param commentId
   * @param userId
   * @return
   */
  def readOneCommentFromOneUser(commentId: Int, userId: Int):Future[Option[Comment]]

  /**
   * read all comments from one user
   * @param userId
   * @return
   */
  def readAllCommentsFromOneUser(userId: Int):Future[Seq[Comment]]

  /**
   * delete an existing comment
   * @param commentId
   * @return
   */
  def delete(commentId: Int):Future[Unit]

  /**
   * find existing comment by id
   * @param commentId
   * @return
   */
  def find(commentId: Int):Future[Option[Comment]]


}

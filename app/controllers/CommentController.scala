package controllers


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Authorization, Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{User, Comment}
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, Controller, Action}
import repositories.CommentRepository

import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

/**
 * Created by Christoph on 19.01.2016.
 */
class CommentController @Inject()(commentRepository: CommentRepository,
                                  val messagesApi: MessagesApi,
                                  val env: Environment[User, JWTAuthenticator]) extends Silhouette[User,JWTAuthenticator]{

  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  // everybody is able to read comments
  def listAllCommentsFromOneRestaurant(restaurantId: Int, page:Option[Int],size:Option[Int]) = Action.async {
    commentRepository.readAllCommentsFromOneRestaurant(restaurantId, page.getOrElse(0),size.getOrElse(100))
      .map(c => Ok(Json toJson c))
  }

  // list all comments from the logged in user
  def listAllCommentsFromOneUser() = SecuredAction.async {
    implicit request => commentRepository.readAllCommentsFromOneUser(request.identity.id.get).map(c => Ok(Json toJson c))
  }

  // only admins can delete all comments
  def deleteCommentAsAdmin(commentId: Int) = SecuredAction(IsAdmin()).async {
    commentRepository.delete(commentId).map(d => Ok(Json.obj("message" -> "Comment was successfully deleted")))
  }

  def createComment() = SecuredAction.async(parse.json) {
    implicit request => request.body.validate[Comment].map {
      comment => commentRepository.save(comment,request.identity.id.get).map(c => Ok(Json toJson c))
    }.recoverTotal {
      case error => Future.successful(
        BadRequest(Json.obj("message" -> "Could not create comment"))
      )
    }

  }

  // user only can delete their own comments
  def deleteComment(commentId: Int) = SecuredAction.async {
      implicit request => commentRepository.readOneCommentFromOneUser(commentId, request.identity.id.get).flatMap{
        case Some(c) => commentRepository.delete(commentId).map(d => Ok(Json.obj("message" -> "Comment was successfully deleted")))
        case None => Future.successful(BadRequest(Json.obj("message" -> "Comment could not be deleted")))
      }
    }

  // user only can update their own comments
  def updateComment(commentId: Int) = SecuredAction.async(parse.json) {
    implicit request => commentRepository.readOneCommentFromOneUser(commentId, request.identity.id.get).flatMap{
      case Some(c) => request.body.validate[Comment].map {
        comment => commentRepository.save(comment,request.identity.id.get).map(c => Ok(Json toJson c))
      }.recoverTotal {
      case error => Future.successful(
      BadRequest(Json.obj("message" -> "Error while updating"))
      )
      }
      case None => Future.successful(BadRequest(Json.obj("message" -> "Comment could not be deleted")))
    }
  }


}

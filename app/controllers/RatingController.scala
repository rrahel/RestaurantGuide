package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Authorization, Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{Rating, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json}
import repositories.RatingRepository
import play.api.mvc.{Request, Controller, Action}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Created by Christoph on 22.01.2016.
 */
class RatingController @Inject() (ratingRepository: RatingRepository,
                                  val messagesApi: MessagesApi,
                                   val env: Environment[User, JWTAuthenticator]) extends Silhouette[User,JWTAuthenticator] {

  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  // not necessary because rating of restaurant is already saved in restaurant table
/*  def readRatingFromRestaurant(restaurantID: Int) = Action.async {
    ratingRepository.readAllRatingsFromOneRestaurant(restaurantID)
      .map(r => Ok(Json toJson r))
  }*/

  def createRating() = SecuredAction.async(parse.json) {
    implicit request => request.body.validate[Rating].map {
      rating => ratingRepository.save(rating,request.identity.id.get).map(r => Ok(Json toJson r))
    }.recoverTotal {
      case error => Future.successful(
        BadRequest(Json.obj("message" -> "Could not create rating"))
      )
    }
  }

  def updateRating(ratingId: Int) = SecuredAction.async(parse.json) {
    implicit request => ratingRepository.readOneRatingsFromOneUser(ratingId, request.identity.id.get).flatMap{
      case Some(r) => request.body.validate[Rating].map {
        rating => ratingRepository.save(rating,request.identity.id.get).map(r => Ok(Json toJson r))
      }.recoverTotal {
        case error => Future.successful(
          BadRequest(Json.obj("message" -> "Error while updating"))
        )
      }
      case None => Future.successful(BadRequest(Json.obj("message" -> "Rating could not be updated")))
    }
  }

  def topRestaurantsFromUser() = SecuredAction.async {
    implicit request => ratingRepository.readTop6RatingsFromOneUser(request.identity.id.get).map(r => Ok(Json toJson r))
  }

  def topRestaurants() = Action.async {
    implicit request => ratingRepository.readTop6Ratings().map(r => Ok(Json toJson r))
  }



}

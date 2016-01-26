package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Authorization, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Request}
import scala.concurrent.Future
import repositories.RestaurantRepository
import models.{Restaurant, User}

/**
 * Created by cemirrah13 on 19.01.2016.
 */
class RestaurantController @Inject()(restaurantRepository: RestaurantRepository,
                                      val messagesApi: MessagesApi,
                                      val env: Environment[User,JWTAuthenticator])extends Silhouette[User,JWTAuthenticator]{


  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  case class IsUser() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("USER"))
  }

  def all() = Action.async{
    restaurantRepository.all()
      .map(l => Ok(Json.toJson(l)))
  }

  def allWithPages(page:Option[Int], size:Option[Int]) = Action.async {
    restaurantRepository.all(page.getOrElse(0),size.getOrElse(100))
      .map(g => Ok(Json toJson g))
  }

  def count() = Action.async{
    restaurantRepository.count()
      .map(count => Ok(Json toJson count))
  }

  def create = SecuredAction(IsAdmin()).async(parse.json) {
    implicit request =>
      restaurantRepository.create(request.body.validate[Restaurant].get)
        .map(newRes => Ok(Json.toJson(newRes)))
  }

  def findResById(resId:Int) = Action.async {
    restaurantRepository.find(resId)
      .map { case Some(res) => Ok(Json.toJson(res))
      case None => NotFound(Json obj "message" -> s"Restaurant with id=$resId was not found")
      }
  }

  def deleteRestaurant(resId: Int) = SecuredAction(IsAdmin()).async{
    restaurantRepository.delete(resId).map(_ => Ok(Json obj "message" -> s"Restaurant was successfully deleted"))
  }

  def findRestByName(name: String) =  Action.async {
    restaurantRepository.findResByName(name).map(c => Ok(Json toJson c))
  }

}

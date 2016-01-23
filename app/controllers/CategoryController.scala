package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Authorization, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Request}
import scala.concurrent.Future
import repositories.{CategoryRepository, RestaurantRepository}
import models.{Restaurant, Category, User}

/**
 * Created by cemirrah13 on 23.01.2016.
 */
class CategoryController @Inject()(categoryRepository: CategoryRepository,
                                    val messagesApi: MessagesApi,
                                    val env: Environment[User, JWTAuthenticator])extends Silhouette[User,JWTAuthenticator]{

  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  case class IsUser() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("USER"))
  }

  def all() = Action.async{
    categoryRepository.all()
      .map(l => Ok(Json.toJson(l)))
  }

  def create = SecuredAction(IsAdmin()).async(parse.json) {
    implicit request =>
      categoryRepository.create(request.body.validate[Category].get)
        .map(newRes => Ok(Json.toJson(newRes)))
  }

  def delete(catId: Int) = SecuredAction(IsAdmin()).async{
    categoryRepository.delete(catId).map(_ => Ok(Json obj "message" -> s"Category was successfully deleted"))
  }
  /*  def find(catId:Int) = Action.async {
      categoryRepository.find(catId)
        .map { case Some(res) => Ok(Json.toJson(res))
        case None => NotFound(Json obj "message" -> s"Category with id=$catId was not found")
        }
    }*/


}

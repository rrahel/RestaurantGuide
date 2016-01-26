package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services.UserIdentityService
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Request}
import _root_.repositories.UserRepository
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.{SignUpInfo, User}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models.UserPreview._

/*import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers._
import play.api.libs.functional.syntax._
import models.{SignInInfo, SignUpInfo, User}
import models.services.UserIdentityService
import play.api.Configuration
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.mvc.Action
import net.ceedubs.ficus.Ficus._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
*/
/**
 * Created by salho on 12.08.15.
 */
class UserController @Inject()(val messagesApi: MessagesApi,
                               val env: Environment[User, JWTAuthenticator],
                               userRepository: UserRepository,
                               userService: UserIdentityService,
                               authInfoRepository: AuthInfoRepository,
                               passwordHasher: PasswordHasher,
                               credentialsProvider: CredentialsProvider) extends Silhouette[User, JWTAuthenticator] {

  case class IsAdmin() extends Authorization[User, JWTAuthenticator] {
    override def isAuthorized[B](identity: User, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] =
      Future.successful(identity.roles.contains("ADMINISTRATOR"))
  }

  def list = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.all.map(users => Ok(Json toJson users)))

  def listWithPages(page: Int, pageSize: Int) = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.all(page,pageSize).map(users => Ok(Json toJson users)))

  def delete(id: Int) = SecuredAction(IsAdmin()
  ).async { implicit request =>
    userRepository.delete(id).map(_ => Ok(Json obj "message" -> s"User with id $id was deleted"))
  }

  def count = SecuredAction(IsAdmin()
  ).async(implicit request => userRepository.count.map(count => Ok(Json toJson count)))



  def update = SecuredAction.async(parse.json) {
    implicit request => userRepository.find(request.identity.id.get).flatMap{
      case Some(u) => request.body.validate[User].map{
        user => userRepository.save(user).map(u => Ok(Json toJson u))
      }.recoverTotal{
        case error => Future.successful(BadRequest(Json.obj("message" -> "Error while updating")))
      }
      case None => Future.successful(BadRequest(Json.obj("message" -> "User was not found")))
    }
  }



  def save = SecuredAction(IsAdmin()).async(parse.json) { implicit request =>
    request.body.validate[SignUpInfo].map { data =>
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
        case None =>
          val authInfo = passwordHasher.hash(data.password)
          val user = User(None,data.firstname,data.lastname,data.email,loginInfo.providerID,loginInfo.providerKey)
          for {
            user <- userService.save(user)
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
          } yield {
            env.eventBus.publish(SignUpEvent(user, request, request2Messages))
            val successMessage = "Create new user: " + data.email
            Ok(Json.obj("message" -> successMessage))
          }
      }

    }.recoverTotal {
      case error => Future.successful(BadRequest(Json.obj("message" -> Messages("invalid.data"))))
    }
  }

}

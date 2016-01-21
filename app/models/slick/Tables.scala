package models.slick


import javax.xml.ws.BindingProvider

import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.{Restaurant, RestaurantPreview, UserPreview, User, Comment, Rating}
import slick.driver.H2Driver.api._
import slick.lifted.TableQuery
import scala.language.implicitConversions

/**
 * Created by salho on 03.08.15.
 */


class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def firstname = column[String]("FIRSTNAME")

  def lastname = column[String]("LASTNAME")

  def email = column[String]("EMAIL")

  def image = column[Array[Byte]]("IMAGE")

  def providerID = column[String]("PROVIDER_ID")

  def providerKey = column[String]("PROVIDER_KEY")

  def * = (id.?, firstname, lastname, email, image.?, providerID, providerKey) <>(User.withoutRoles, User.toTuple)

  def preview = (id, firstname, lastname, email) <>((UserPreview.apply _).tupled,UserPreview.unapply)
}

case class DBRole(id: Option[Int], userId: Int, role: String)

class Roles(tag: Tag) extends Table[DBRole](tag, "ROLES") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def userID = column[Int]("USER_ID")

  def role = column[String]("ROLE")

  def * = (id.?, userID, role) <>(DBRole.tupled, DBRole.unapply)
}

object Users {
  val users = TableQuery[Users]
}

case class DBPasswordInfo(
                           hasher: String,
                           password: String,
                           salt: Option[String],
                           userID: Int)

class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "PASSWORDINFO") {

  def id = column[Int]("ID",O.PrimaryKey,O.AutoInc)

  def hasher = column[String]("HASHER")

  def password = column[String]("PASSWORD")

  def salt = column[Option[String]]("SALT")

  def userID = column[Int]("USER_ID")

  def * = (hasher, password, salt, userID) <>((DBPasswordInfo.apply _).tupled, DBPasswordInfo.unapply)


}

object DBPasswordInfo {
  def passwordInfo2db(userID: Int, pwInfo: PasswordInfo) = DBPasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt,userID)
  implicit def db2PasswordInfo(pwInfo: DBPasswordInfo): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
  implicit def dbTableElement2PasswordInfo(pwInfo: PasswordInfos#TableElementType): PasswordInfo = new PasswordInfo(pwInfo.hasher,pwInfo.password,pwInfo.salt)
}

class Restaurants(tag: Tag) extends Table[Restaurant](tag, "RESTAURANTS"){
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def description = column[String]("DESCRIPTION")
  def category = column[String]("CATEGORY")
  def phone = column[String]("PHONE")
  def email = column[String]("EMAIL")
  def image = column[Array[Byte]]("IMAGE")
  def menu = column[Array[Byte]]("MENU")
  def website = column[String]("WEBSITE")
  def rating = column[Double]("RATING")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def zip = column[String]("ZIP")
  def lat = column[Double]("LAT")
  def lng = column[Double]("LNG")
  def * = (id.?, name, description.?, category, phone.?, email.?, image.?, menu.?, website.?, rating.?, street, city, zip, lat, lng) <> (Restaurant.tupled, Restaurant.toTuple)
  def preview = (id.?, name, image.?, rating.?, city) <> (RestaurantPreview.tupled, RestaurantPreview.toTuple)
}

object Restaurants{
  val restaurants = TableQuery[Restaurants]
}

class Comments(tag: Tag) extends Table[Comment](tag, "COMMENTS"){
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def content = column[String]("CONTENT")
  def userId = column[Int]("USER_ID")
  def restaurantId = column[Int]("RESTAURANT_ID")
  def * = (id.?, content, userId, restaurantId) <> (Comment.tupled, Comment.toTuple)
  def userFK = foreignKey("USER_FK", userId, Users.users)(u => u.id)
  def restaurantFK = foreignKey("RESTAURANT_FK", restaurantId, Restaurants.restaurants)(r => r.id)

}

object Comments{
  val comments = TableQuery[Comments]
}

class Ratings(tag: Tag) extends Table[Rating](tag, "RATINGS"){
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def rating = column[Double]("RATING")
  def userId = column[Int]("USER_ID")
  def restaurantId = column[Int]("RESTAURANT_ID")
  def * = (id.?, rating, userId, restaurantId) <> (Rating.tupled, Rating.toTuple)
  def userFK = foreignKey("USER_FK", userId, Users.users)(u => u.id)
  def restaurantFK = foreignKey("RESTAURANT_FK", restaurantId, Restaurants.restaurants)(r => r.id)

}

object Ratings{
  val ratings = TableQuery[Ratings]
}

package models

import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by Christoph on 14.01.2016.
 */
case class Restaurant(id: Option[Int], name: String, description: Option[String],
                      category: Int, phone: Option[String], email: Option[String],
                      website: Option[String], rating: Option[Double], street: String,
                      city: String, zip: String, lat: Double, lng: Double)

object Restaurant {

  implicit val restaurantFormat = Json.format[Restaurant]

  def tupled(t: (Option[Int], String, Option[String], Int, Option[String], Option[String], Option[String],
    Option[Double], String, String, String, Double, Double)) =
    Restaurant(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)

  def toTuple(r: Restaurant) = Some((r.id, r.name, r.description, r.category, r.phone, r.email,
    r.website, r.rating, r.street, r.city, r.zip, r.lat, r.lng))

}

case class RestaurantPreview(id: Option[Int], name: String, rating: Option[Double], city: String)

object RestaurantPreview {

  implicit val restaurantPreviewFormat = Json.format[RestaurantPreview]

  def tupled(t: (Option[Int], String, Option[Double], String)) =
    RestaurantPreview(t._1, t._2, t._3, t._4)

  def toTuple(r: RestaurantPreview) = Some((r.id, r.name, r.rating, r.city))

}

package models

import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by Christoph on 14.01.2016.
 */
case class Rating(id: Option[Int], rating: Double, userId: Int, restaurantId: Int)

object Rating{
  implicit val ratingFormat = Json.format[Rating]

  def tupled(t: (Option[Int], Double, Int, Int)) = Rating(t._1, t._2, t._3, t._4)

  def toTuple(r: Rating) = Some((r.id, r.rating, r.userId, r.restaurantId))
}

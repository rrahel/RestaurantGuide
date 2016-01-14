package models

import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Created by Christoph on 14.01.2016.
 */
case class Comment(id: Option[Int], content: String, userId: Int, restaurantId: Int)

object Comment {
  implicit val commentFormat = Json.format[Comment]

  def tupled(t: (Option[Int], String, Int, Int)) = Comment(t._1, t._2, t._3, t._4)

  def toTuple(c: Comment) = Some((c.id, c.content, c.userId, c.restaurantId))
}
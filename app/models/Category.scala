package models

import play.api.libs.json.Json

/**
 * Created by Christoph on 21.01.2016.
 */
case class Category(id: Option[Int], name: String)

object Category {

  implicit val categoryFormat = Json.format[Category]

  def tupled(t: (Option[Int], String)) = Category(t._1, t._2)

  def toTuple(c: Category) = Some((c.id, c.name))


}
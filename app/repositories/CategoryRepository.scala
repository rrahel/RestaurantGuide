package repositories

import models.{Restaurant, Category}
import models.slick.Restaurants

import scala.concurrent.Future

/**
 * Created by Christoph on 21.01.2016.
 */
trait CategoryRepository {

  /**
   * create a new category
   * @param category
   * @return
   */
  def create(category: Category):Future[Category]

  def delete(categoryId: Int):Future[Unit]

  def find(categoryId: Int):Future[Seq[Restaurant]]

  def all():Future[Seq[Category]]

  def allByCategory(categoryId: Int): Future[Seq[Restaurant]]

  def count(categoryId: Int):Future[Int]




}

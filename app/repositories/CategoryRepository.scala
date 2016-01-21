package repositories

import models.Category

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

}

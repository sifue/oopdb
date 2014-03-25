package tablegateway
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

object TableGatewayExample {
  def main(args: Array[String]) {
    Database.forURL("jdbc:h2:itemdb;DATABASE_TO_UPPER=false", driver = "org.h2.Driver") withSession {
      implicit session =>
        SpotFinder.find("アプカレの卵").foreach(println)
    }
  }
}

object SpotFinder {
  def find(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
      val spotsForDrop = for(((i, r), s) <- dropItems.filter(_.name === itemName)
        leftJoin dropItemSpotRelations on (_.id  === _.dropItemId)
        leftJoin spots on ( _._2.spotId === _.id)
      ) yield s
    spotsForDrop.list().map(s => Spot(s._1, s._2, s._3, s._4))
  }

  val dropItems = TableQuery[DropItems]
  val spots = TableQuery[Spots]
  val dropItemSpotRelations = TableQuery[DropItemSpotRelations]
}

case class Spot(id: Int, name: String, x: Int, y: Int)

class DropItems(tag: Tag) extends Table[(Int, String, Int, String)](tag, "drop_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def enemyName = column[String]("enemy_name")
  def * = (id, name, requireLevel, enemyName)
}

class Spots(tag: Tag) extends Table[(Int, String, Int, Int)](tag, "spots") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def x = column[Int]("x")
  def y = column[Int]("y")
  def * = (id, name, x, y)
}

class DropItemSpotRelations(tag: Tag) extends Table[(Int, Int)](tag, "drop_item_spot_relations") {
  def dropItemId = column[Int]("drop_item_id")
  def spotId = column[Int]("spot_id")
  def * = (dropItemId, spotId)
}

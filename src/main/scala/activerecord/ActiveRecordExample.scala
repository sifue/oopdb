package activerecord
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

object ActiveRecordExample {
  def main(args: Array[String]) {
    Database.forURL("jdbc:h2:itemdb;DATABASE_TO_UPPER=false", driver = "org.h2.Driver") withSession {
      implicit session =>
        SpotFinder.find("アプカレの卵").foreach(println)
    }
  }
}

object SpotFinder {
  def find(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    val dropItem = DropItem.findByName(itemName)
    val dropItemSpotRelations = dropItem.toSeq.map(i => DropItemSpotRelation.findByDropItemId(i.id)).flatten
    val spotsForDrop = dropItemSpotRelations.map(s => Spot.find(s.spotId).toSeq).flatten
    spotsForDrop
  }
}

case class DropItem(id: Int, name: String, requireLevel: Int, enemyName: String)
object DropItem {
  private val table = TableQuery[DropItems]
  def findByName(name: String)(implicit session: H2Driver.backend.Session): Option[DropItem] = {
    DropItem.table.filter(_.name === name).list().headOption
  }
}

case class DropItemSpotRelation(dropItemId: Int, spotId: Int)
object DropItemSpotRelation {
  private val table = TableQuery[DropItemSpotRelations]
  def findByDropItemId(id: Int)(implicit session: H2Driver.backend.Session): Seq[DropItemSpotRelation] = {
    DropItemSpotRelation.table.filter(_.dropItemId === id).list()
  }
}

case class Spot(id: Int, name: String, x: Int, y: Int)
object Spot {
  private val  table = TableQuery[Spots]
  def find(spotId: Int)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    Spot.table.filter(_.id === spotId).list()
  }
}

class DropItems(tag: Tag) extends Table[activerecord.DropItem](tag, "drop_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def enemyName = column[String]("enemy_name")
  def * = (id, name, requireLevel, enemyName) <> ((activerecord.DropItem.apply _).tupled, activerecord.DropItem.unapply)
}

class Spots(tag: Tag) extends Table[activerecord.Spot](tag, "spots") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def x = column[Int]("x")
  def y = column[Int]("y")
  def * = (id, name, x, y) <> ((activerecord.Spot.apply _).tupled, activerecord.Spot.unapply)
}

class DropItemSpotRelations(tag: Tag) extends Table[activerecord.DropItemSpotRelation](tag, "drop_item_spot_relations") {
  def dropItemId = column[Int]("drop_item_id")
  def spotId = column[Int]("spot_id")
  def * = (dropItemId, spotId) <> ((activerecord.DropItemSpotRelation.apply _).tupled, activerecord.DropItemSpotRelation.unapply)
}

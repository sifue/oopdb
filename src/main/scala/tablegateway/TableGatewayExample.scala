package tablegateway
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

object TableGatewayExample {
  def main(args: Array[String]) {
    Database.forURL("jdbc:h2:itemdb;DATABASE_TO_UPPER=false", driver = "org.h2.Driver") withSession {
      implicit session =>
        SpotFinder.findSpot("アプカレの卵").foreach(println)
    }
  }
}

object SpotFinder {
  def findSpot(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
      val spotsForDrop = for(((i, r), s) <- dropItems.filter(_.name === itemName)
        leftJoin dropItemSpotRelations on (_.id  === _.dropItemId)
        leftJoin spots on ( _._2.spotId === _.id)
      ) yield s

      val spotsForGathering = for(((i, r), s) <- gatheringItems.filter(_.name === itemName)
        leftJoin gatheringItemSpotRelations on (_.id  === _.gatheringItemId)
        leftJoin spots on ( _._2.spotId === _.id)
      ) yield s

      spotsForDrop.list() ++ spotsForGathering.list()
  }

  val dropItems = TableQuery[DropItems]
  val gatheringItems = TableQuery[GatheringItems]
  val spots = TableQuery[Spots]
  val dropItemSpotRelations = TableQuery[DropItemSpotRelations]
  val gatheringItemSpotRelations = TableQuery[GatheringItemSpotRelations]
}

case class DropItem(id: Int, name: String, requireLevel: Int, enemyName: String)
case class DropItemSpotRelation(dropItemId: Int, spotId: Int)
case class GatheringItem(id: Int, name: String, requireLevel: Int, requireGathererClass: String)
case class GatheringItemSpotRelation(gatheringItemId: Int, spotId: Int)
case class Spot(id: Int, name: String, x: Int, y: Int)

class DropItems(tag: Tag) extends Table[tablegateway.DropItem](tag, "drop_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def enemyName = column[String]("enemy_name")
  def * = (id, name, requireLevel, enemyName) <> (tablegateway.DropItem.tupled, tablegateway.DropItem.unapply)
}

class GatheringItems(tag: Tag) extends Table[tablegateway.GatheringItem](tag, "gathering_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def requireGathererClass = column[String]("require_gatherer_class")
  def * = (id, name, requireLevel, requireGathererClass) <> (tablegateway.GatheringItem.tupled, tablegateway.GatheringItem.unapply)
}

class Spots(tag: Tag) extends Table[tablegateway.Spot](tag, "spots") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def x = column[Int]("x")
  def y = column[Int]("y")
  def * = (id, name, x, y) <> (tablegateway.Spot.tupled, tablegateway.Spot.unapply)
}

class DropItemSpotRelations(tag: Tag) extends Table[tablegateway.DropItemSpotRelation](tag, "drop_item_spot_relations") {
  def dropItemId = column[Int]("drop_item_id")
  def spotId = column[Int]("spot_id")
  def * = (dropItemId, spotId) <> (tablegateway.DropItemSpotRelation.tupled, tablegateway.DropItemSpotRelation.unapply)
}

class GatheringItemSpotRelations(tag: Tag) extends Table[tablegateway.GatheringItemSpotRelation](tag, "gathering_item_spot_relations") {
  def gatheringItemId = column[Int]("gathering_item_id")
  def spotId = column[Int]("spot_id")
  def * = (gatheringItemId, spotId) <> (tablegateway.GatheringItemSpotRelation.tupled, tablegateway.GatheringItemSpotRelation.unapply)
}

package datamapper
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

object DataMapperExample {
  def main(args: Array[String]) {
    Database.forURL("jdbc:h2:itemdb;DATABASE_TO_UPPER=false", driver = "org.h2.Driver") withSession {
      implicit session =>
        new SpotPrinter().print("アプカレの卵")
    }
  }
}

class SpotPrinter {
  val spotFinder = new SpotFinder()
  def print(itemName: String)(implicit session: H2Driver.backend.Session) = {
    spotFinder.find(itemName).foreach(println)
  }
}

class SpotFinder {
  private val dataMapper = new SpotDataMapper()
  def find(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    dataMapper.findSpot(itemName)
  }
}

class SpotDataMapper {
  private val dropItems = TableQuery[DropItems]
  private val gatheringItems = TableQuery[GatheringItems]
  private val spots = TableQuery[Spots]
  private val dropItemSpotRelations = TableQuery[DropItemSpotRelations]
  private val gatheringItemSpotRelations = TableQuery[GatheringItemSpotRelations]

  def findSpot(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    findSpotForDrop(itemName) ++ findSpotForGathering(itemName)
  }

  def findSpotForDrop(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    val spotsForDrop = for(((i, r), s) <- dropItems.filter(_.name === itemName)
      leftJoin dropItemSpotRelations on (_.id  === _.dropItemId)
      leftJoin spots on ( _._2.spotId === _.id)
    ) yield s
    spotsForDrop.list()
  }

  def findSpotForGathering(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    val spotsForGathering = for(((i, r), s) <- gatheringItems.filter(_.name === itemName)
      leftJoin gatheringItemSpotRelations on (_.id  === _.gatheringItemId)
      leftJoin spots on ( _._2.spotId === _.id)
    ) yield s
     spotsForGathering.list()
  }
}

class DropItems(tag: Tag) extends Table[datamapper.DropItem](tag, "drop_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def enemyName = column[String]("enemy_name")
  def * = (id, name, requireLevel, enemyName) <> (datamapper.DropItem.tupled, datamapper.DropItem.unapply)
}

class GatheringItems(tag: Tag) extends Table[datamapper.GatheringItem](tag, "gathering_items") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def requireLevel = column[Int]("require_level")
  def requireGathererClass = column[String]("require_gatherer_class")
  def * = (id, name, requireLevel, requireGathererClass) <> (datamapper.GatheringItem.tupled, datamapper.GatheringItem.unapply)
}

class Spots(tag: Tag) extends Table[datamapper.Spot](tag, "spots") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def x = column[Int]("x")
  def y = column[Int]("y")
  def * = (id, name, x, y) <> (datamapper.Spot.tupled, datamapper.Spot.unapply)
}

class DropItemSpotRelations(tag: Tag) extends Table[datamapper.DropItemSpotRelation](tag, "drop_item_spot_relations") {
  def dropItemId = column[Int]("drop_item_id")
  def spotId = column[Int]("spot_id")
  def * = (dropItemId, spotId) <> (datamapper.DropItemSpotRelation.tupled, datamapper.DropItemSpotRelation.unapply)
}

class GatheringItemSpotRelations(tag: Tag) extends Table[datamapper.GatheringItemSpotRelation](tag, "gathering_item_spot_relations") {
  def gatheringItemId = column[Int]("gathering_item_id")
  def spotId = column[Int]("spot_id")
  def * = (gatheringItemId, spotId) <> (datamapper.GatheringItemSpotRelation.tupled, datamapper.GatheringItemSpotRelation.unapply)
}

case class DropItem(id: Int, name: String, requireLevel: Int, enemyName: String)
case class DropItemSpotRelation(dropItemId: Int, spotId: Int)
case class GatheringItem(id: Int, name: String, requireLevel: Int, requireGathererClass: String)
case class GatheringItemSpotRelation(gatheringItemId: Int, spotId: Int)
case class Spot(id: Int, name: String, x: Int, y: Int)

package repository

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

package application {
object repositoryExample {
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
  private val repository = new domain.SpotRepository()

  def find(itemName: String)(implicit session: H2Driver.backend.Session): Seq[domain.Spot] = {
    repository.findSpot(itemName)
  }
}
}

package domain {
import repository.infrastracture._

case class Spot(id: Int, name: String, x: Int, y: Int)

class SpotRepository {
  private val dropItems = TableQuery[DropItems]
  private val gatheringItems = TableQuery[GatheringItems]
  private val spots = TableQuery[Spots]
  private val dropItemSpotRelations = TableQuery[DropItemSpotRelations]
  private val gatheringItemSpotRelations = TableQuery[GatheringItemSpotRelations]

  def findSpot(itemName: String)(implicit session: H2Driver.backend.Session): Seq[domain.Spot] = {
    findSpotForDrop(itemName) ++ findSpotForGathering(itemName)
  }

  def findSpotForDrop(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    val spotsForDrop = for(((i, r), s) <- dropItems.filter(_.name === itemName)
      leftJoin dropItemSpotRelations on (_.id  === _.dropItemId)
      leftJoin spots on ( _._2.spotId === _.id)
    ) yield s
    spotsForDrop.list().map(OrmSpot.unapply).toList.flatten.map(Spot.tupled)
  }

  def findSpotForGathering(itemName: String)(implicit session: H2Driver.backend.Session): Seq[Spot] = {
    val spotsForGathering = for(((i, r), s) <- gatheringItems.filter(_.name === itemName)
      leftJoin gatheringItemSpotRelations on (_.id  === _.gatheringItemId)
      leftJoin spots on ( _._2.spotId === _.id)
    ) yield s
    spotsForGathering.list().map(toDomainEntity)
  }

  def toDomainEntity(orm: OrmSpot): Spot = {
    OrmSpot.unapply(orm).map(Spot.tupled).getOrElse(
      throw new IllegalArgumentException("Unapply method is not available.")
    )
  }
}
}

 package infrastracture {

 class DropItems(tag: Tag) extends Table[OrmDropItem](tag, "drop_items") {
   def id = column[Int]("id", O.PrimaryKey)
   def name = column[String]("name")
   def requireLevel = column[Int]("require_level")
   def enemyName = column[String]("enemy_name")
   def * = (id, name, requireLevel, enemyName) <> (OrmDropItem.tupled, OrmDropItem.unapply)
 }

 class GatheringItems(tag: Tag) extends Table[OrmGatheringItem](tag, "gathering_items") {
   def id = column[Int]("id", O.PrimaryKey)
   def name = column[String]("name")
   def requireLevel = column[Int]("require_level")
   def requireGathererClass = column[String]("require_gatherer_class")
   def * = (id, name, requireLevel, requireGathererClass) <> (OrmGatheringItem.tupled, OrmGatheringItem.unapply)
 }

 class Spots(tag: Tag) extends Table[OrmSpot](tag, "spots") {
   def id = column[Int]("id", O.PrimaryKey)
   def name = column[String]("name")
   def x = column[Int]("x")
   def y = column[Int]("y")
   def * = (id, name, x, y) <> (OrmSpot.tupled, OrmSpot.unapply)
 }

 class DropItemSpotRelations(tag: Tag) extends Table[OrmDropItemSpotRelation](tag, "drop_item_spot_relations") {
   def dropItemId = column[Int]("drop_item_id")
   def spotId = column[Int]("spot_id")
   def * = (dropItemId, spotId) <> (OrmDropItemSpotRelation.tupled, OrmDropItemSpotRelation.unapply)
 }

 class GatheringItemSpotRelations(tag: Tag) extends Table[OrmGatheringItemSpotRelation](tag, "gathering_item_spot_relations") {
   def gatheringItemId = column[Int]("gathering_item_id")
   def spotId = column[Int]("spot_id")
   def * = (gatheringItemId, spotId) <> (OrmGatheringItemSpotRelation.tupled, OrmGatheringItemSpotRelation.unapply)
 }

 case class OrmDropItem(id: Int, name: String, requireLevel: Int, enemyName: String)
 case class OrmDropItemSpotRelation(dropItemId: Int, spotId: Int)
 case class OrmGatheringItem(id: Int, name: String, requireLevel: Int, requireGathererClass: String)
 case class OrmGatheringItemSpotRelation(gatheringItemId: Int, spotId: Int)
 case class OrmSpot(id: Int, name: String, x: Int, y: Int)
 }


package repository

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

package ui {

object repositoryExample {
  def main(args: Array[String]) {
    Database.forURL("jdbc:h2:itemdb;DATABASE_TO_UPPER=false", driver = "org.h2.Driver") withSession {
      implicit session =>
        new SpotPrinter().print("アプカレの卵")
    }
  }
}

class SpotPrinter {
  val spotFinder = new application.SpotFinder()

  def print(itemName: String)(implicit session: H2Driver.backend.Session) = {
    spotFinder.find(itemName).foreach(println)
  }
}
}

package application {

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
  private val spots = TableQuery[Spots]
  private val dropItemSpotRelations = TableQuery[DropItemSpotRelations]

  def findSpot(itemName: String)(implicit session: H2Driver.backend.Session): Seq[domain.Spot] = {
    val spotsForDrop = for(((i, r), s) <- dropItems.filter(_.name === itemName)
      leftJoin dropItemSpotRelations on (_.id  === _.dropItemId)
      leftJoin spots on ( _._2.spotId === _.id)
    ) yield s
    spotsForDrop.list().map(OrmSpot.unapply).toList.flatten.map(Spot.tupled)
  }

  def toDomainEntity(orm: OrmSpot): Spot = {
    OrmSpot.unapply(orm).map(Spot.tupled).get
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

 case class OrmDropItem(id: Int, name: String, requireLevel: Int, enemyName: String)
 case class OrmDropItemSpotRelation(dropItemId: Int, spotId: Int)
 case class OrmSpot(id: Int, name: String, x: Int, y: Int)
 }


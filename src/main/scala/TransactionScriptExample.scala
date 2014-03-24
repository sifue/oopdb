import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

object TransactionScriptExample {
  def main(args: Array[String]) {
    SpotFinder.findSpot("アプカレの卵").foreach(println)
  }

  case class Spot(id: Int, name: String, x: Int, y: Int)

  object SpotFinder {
    def findSpot(itemName: String): Seq[Spot] = {
      Database.forURL("jdbc:h2:itemdb", driver = "org.h2.Driver") withSession {
        implicit session =>
          implicit val spotConverter = GetResult(r => Spot(r.<<, r.<<, r.<<, r.<<))
          val queryForDrop = Q.query[String, Spot]( """
        |select
        |    spots.id,
        |    spots.name,
        |    spots.x,
        |    spots.y
        |from
        |    (
        |        drop_items
        |        left join
        |            drop_item_spot_relations
        |        on  drop_items.id = drop_item_spot_relations.drop_item_id
        |    )
        |    left join
        |        spots
        |    on  drop_item_spot_relations.spot_id = spots.id
        |where
        |    drop_items.name = ?""".stripMargin)
          val dropItems = queryForDrop.list(itemName)
          val queryForGathering = Q.query[String, Spot]("""
        |select
        |    spots.id,
        |    spots.name,
        |    spots.x,
        |    spots.y
        |from
        |    (
        |        gathering_items
        |        left join
        |            gathering_item_spot_relations
        |        on  gathering_items.id = gathering_item_spot_relations.gathering_item_id
        |    )
        |    left join
        |        spots
        |    on  gathering_item_spot_relations.spot_id = spots.id
        |where
        |    gathering_items.name = ?""".stripMargin)
          val gatheringItems = queryForGathering.list(itemName)
          dropItems ++ gatheringItems
      }
    }
  }

}

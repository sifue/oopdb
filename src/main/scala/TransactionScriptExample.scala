object TransactionScriptExample {
  def main(args: Array[String]) {

    println("こんにちわ")
  }

  trait Item {val id: Int;val name: String;val requireLevel: Int;}
  case class DropItem(id: Int, name: String, requireLevel: Int, enemyName: String) extends Item
  case class GatheringItem(id: Int, name: String, requireLevel: Int, requireGathererClass: String) extends Item
  case class Spot(id: Int, name: String, x: Int, y: Int)



}

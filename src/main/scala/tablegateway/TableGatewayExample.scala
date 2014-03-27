package tablegateway
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver
import java.util.Date
import scala.slick.lifted
import java.sql.Timestamp

object TableGatewayExample extends App {
  Database.forURL("jdbc:h2:db;database_to_upper=false", driver = "org.h2.Driver") withSession {
    implicit session =>
      TagFinder.find("ニコラジ").foreach(println)
  }
}

object TagFinder {
  def find(programTitle: String)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
      val relatedTags = for(((i, r), s) <- programs.filter(_.title === programTitle)
        leftJoin programTagRelations on (_.id  === _.programId)
        leftJoin tags on ( _._2.tagId === _.id)
      ) yield s
    relatedTags.list().map(s => Tag(s._1, s._2, s._3))
  }

  val programs = TableQuery[Programs]
  val tags = TableQuery[Tags]
  val programTagRelations = TableQuery[ProgramTagRelations]
}

case class Tag(id: Int, name: String, isCategory: Boolean)

class Programs(tag: lifted.Tag) extends Table[(Int, String, Timestamp, Timestamp)](tag, "programs") {
  def id = column[Int]("id", O.PrimaryKey)
  def title = column[String]("title")
  def beginTime = column[Timestamp]("begin_time")
  def endTime = column[Timestamp]("end_time")
  def * = (id, title, beginTime, endTime)
}

class Tags(tag: lifted.Tag) extends Table[(Int, String, Boolean)](tag, "tags") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def isCategory = column[Boolean]("is_category")
  def * = (id, name, isCategory)
}

class ProgramTagRelations(tag: lifted.Tag) extends Table[(Int, Int)](tag, "program_tag_relations") {
  def programId = column[Int]("program_id")
  def tagId = column[Int]("tag_id")
  def * = (programId, tagId)
}

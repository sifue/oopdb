package activerecord
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver
import scala.slick.lifted
import java.sql.Timestamp

object ActiveRecordExample extends App {
  Database.forURL("jdbc:h2:db;database_to_upper=false", driver = "org.h2.Driver") withSession {
    implicit session =>
      TagFinder.find("ニコラジ").foreach(println)
  }
}

object TagFinder {
  def find(programTitle: String)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
    val program = Program.findByTitle(programTitle)
    val programTagRelations = program.toSeq.map(i => ProgramTagRelation.findByProgramId(i.id)).flatten
    val relatedTags = programTagRelations.map(s => Tag.find(s.tagId).toSeq).flatten
    relatedTags
  }
}

case class Program(id: Int, title: String, beginTime: Timestamp, endTime: Timestamp)
object Program {
  private val table = TableQuery[Programs]
  def findByTitle(title: String)(implicit session: H2Driver.backend.Session): Option[Program] = {
    Program.table.filter(_.title === title).list().headOption
  }
}

case class ProgramTagRelation(programId: Int, tagId: Int)
object ProgramTagRelation {
  private val table = TableQuery[ProgramTagRelations]
  def findByProgramId(id: Int)(implicit session: H2Driver.backend.Session): Seq[ProgramTagRelation] = {
    ProgramTagRelation.table.filter(_.programId === id).list()
  }
}

case class Tag(id: Int, name: String, isCategory: Boolean)
object Tag {
  private val  table = TableQuery[Tags]
  def find(tagId: Int)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
    Tag.table.filter(_.id === tagId).list()
  }
}

class Programs(tag: lifted.Tag) extends Table[activerecord.Program](tag, "programs") {
  def id = column[Int]("id", O.PrimaryKey)
  def title = column[String]("title")
  def beginTime = column[Timestamp]("begin_time")
  def endTime = column[Timestamp]("end_time")
  def * = (id, title, beginTime, endTime) <> ((activerecord.Program.apply _).tupled, activerecord.Program.unapply)
}

class Tags(tag: lifted.Tag) extends Table[activerecord.Tag](tag, "tags") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def isCategory = column[Boolean]("is_category")
  def * = (id, name, isCategory) <> ((activerecord.Tag.apply _).tupled, activerecord.Tag.unapply)
}

class ProgramTagRelations(tag: lifted.Tag) extends Table[activerecord.ProgramTagRelation](tag, "program_tag_relations") {
  def programId = column[Int]("program_id")
  def tagId = column[Int]("tag_id")
  def * = (programId, tagId) <> ((activerecord.ProgramTagRelation.apply _).tupled, activerecord.ProgramTagRelation.unapply)
}

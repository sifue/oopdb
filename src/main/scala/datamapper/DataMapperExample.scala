package datamapper
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver
import scala.slick.lifted
import java.sql.Timestamp

object DataMapperExample extends App {
  Database.forURL("jdbc:h2:db;database_to_upper=false", driver = "org.h2.Driver") withSession {
    implicit session =>
      new TagFinder().find("ニコラジ").foreach(println)
  }
}

class TagFinder {
  private val dataMapper = new TagDataMapper()
  def find(programTitle: String)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
    dataMapper.findTag(programTitle)
  }
}

class TagDataMapper {
  private val programs = TableQuery[Programs]
  private val tags = TableQuery[Tags]
  private val programTagRelations = TableQuery[ProgramTagRelations]

  def findTag(programTitle: String)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
    val relatedTags = for(((i, r), s) <- programs.filter(_.title === programTitle)
      leftJoin programTagRelations on (_.id  === _.programId)
      leftJoin tags on ( _._2.tagId === _.id)
    ) yield s
    relatedTags.list()
  }
}

class Programs(tag: lifted.Tag) extends Table[datamapper.Program](tag, "programs") {
  def id = column[Int]("id", O.PrimaryKey)
  def title = column[String]("title")
  def beginTime = column[Timestamp]("begin_time")
  def endTime = column[Timestamp]("end_time")
  def * = (id, title, beginTime, endTime) <> (datamapper.Program.tupled, datamapper.Program.unapply)
}

class Tags(tag: lifted.Tag) extends Table[datamapper.Tag](tag, "tags") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def isCategory = column[Boolean]("is_category")
  def * = (id, name, isCategory) <> (datamapper.Tag.tupled, datamapper.Tag.unapply)
}

class ProgramTagRelations(tag: lifted.Tag) extends Table[datamapper.ProgramTagRelation](tag, "program_tag_relations") {
  def programId = column[Int]("program_id")
  def tagId = column[Int]("tag_id")
  def * = (programId, tagId) <> (datamapper.ProgramTagRelation.tupled, datamapper.ProgramTagRelation.unapply)
}

case class Program(id: Int, title: String, beginTime: Timestamp, endTime: Timestamp)
case class ProgramTagRelation(programId: Int, tagId: Int)
case class Tag(id: Int, name: String, isCategory: Boolean)

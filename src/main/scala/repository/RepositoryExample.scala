package repository

import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

// 各パッケージがレイヤを表現
package ui {

object repositoryExample extends App {
  Database.forURL(
    "jdbc:h2:db;database_to_upper=false",
    driver = "org.h2.Driver") withSession {
    implicit session =>
      new TagPrinter().print("ニコラジ")
  }
}

class TagPrinter {
  val tagFinder = new application.TagFinder()
  def print(programTitle: String)
           (implicit session: H2Driver.backend.Session) = {
    tagFinder.find(programTitle).foreach(println)
  }
}

}

package application {

class TagFinder {
  private val repository = new domain.TagRepository()
  def find(programTitle: String)
          (implicit session: H2Driver.backend.Session)
  : Seq[domain.Tag] = {
    repository.findTag(programTitle)
  }
}

}

package domain {

import repository.infrastracture._

// ドメイン層のTagとインフラ層の永続化のためのOrmTagを
// 分離したことで多彩な振る舞いを表現しやすい
case class Tag(id: Int, name: String, isCategory: Boolean) {
  def relatedProgramCount: Int = ???
  def hasNicopediaArticle: Boolean = ???
}

// Tagの永続化をリポジトリが隠蔽する
class TagRepository {
  private val programs = TableQuery[Programs]
  private val tags = TableQuery[Tags]
  private val programTagRelations = TableQuery[ProgramTagRelations]

  def findTag(programTitle: String)
             (implicit session: H2Driver.backend.Session)
  : Seq[domain.Tag] = {
    val relatedTags =
      for (((i, r), s)
           <- programs.filter(_.title === programTitle)
        leftJoin programTagRelations on (_.id === _.programId)
        leftJoin tags on (_._2.tagId === _.id)
      ) yield s
    relatedTags
      .list()
      .map(OrmTag.unapply)
      .toList
      .flatten
      .map(Tag.tupled)
  }

  def toDomainEntity(orm: OrmTag): Tag = {
    OrmTag.unapply(orm).map(Tag.tupled).get
  }
}

}

package infrastracture {

import java.sql.Timestamp
import scala.slick.lifted

class Programs(tag: lifted.Tag)
  extends Table[OrmProgram](tag, "programs") {
  def id = column[Int]("id", O.PrimaryKey)
  def title = column[String]("title")
  def beginTime = column[Timestamp]("begin_time")
  def endTime = column[Timestamp]("end_time")
  def * = (id, title, beginTime, endTime) <>
    (OrmProgram.tupled, OrmProgram.unapply)
}

class Tags(tag: lifted.Tag)
  extends Table[OrmTag](tag, "tags") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def isCategory = column[Boolean]("is_category")
  def * = (id, name, isCategory) <>
    (OrmTag.tupled, OrmTag.unapply)
}

class ProgramTagRelations(tag: lifted.Tag)
  extends Table[OrmProgramTagRelation](tag,
    "program_tag_relations") {
  def programId = column[Int]("program_id")
  def tagId = column[Int]("tag_id")
  def * = (programId, tagId) <>
    (OrmProgramTagRelation.tupled,
      OrmProgramTagRelation.unapply)
}

case class OrmProgram(
                       id: Int,
                       title: String,
                       beginTime: Timestamp,
                       endTime: Timestamp)
case class OrmProgramTagRelation(programId: Int, tagId: Int)
case class OrmTag(id: Int, name: String, isCategory: Boolean)

}


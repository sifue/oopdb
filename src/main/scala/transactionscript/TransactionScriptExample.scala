package transactionscript

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import scala.slick.driver.H2Driver

object TransactionScriptExample extends App {
  Database.forURL("jdbc:h2:db;database_to_upper=false", driver = "org.h2.Driver") withSession {
    implicit session =>
      TagFinder.find("ニコラジ").foreach(println)
  }
}

case class Tag(id: Int, name: String, isCategory: Boolean)

object TagFinder {
  def find(title: String)(implicit session: H2Driver.backend.Session): Seq[Tag] = {
      implicit val tagConverter = GetResult(r => Tag(r.<<, r.<<, r.<<))
      val query = Q.query[String, Tag]( """
      |select
      |    tags.id,
      |    tags.name,
      |    tags.is_category
      |from
      |    (
      |        programs
      |        left join
      |            program_tag_relations
      |        on  programs.id = program_tag_relations.program_id
      |    )
      |    left join
      |        tags
      |    on  program_tag_relations.tag_id = tags.id
      |where
      |    programs.title = ?""".stripMargin)
      val relatedTags = query.list(title)
      relatedTags
  }
}

package services

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

import models.LatinText

object LatinTextManager {
  val parser = for {
    id <- long("id")
    title <- str("title")
    content <- str("content")
    comment <- str("comment")
  } yield LatinText(Some(id), title, content, comment)

  def save(lt: LatinText) = DB.withConnection {implicit c =>
    lt.id.fold {
      val id = SQL("""INSERT INTO latin_text (title, content, comment) VALUES ({title}, {content}, {comment})""").on('title -> lt.title, 'content -> lt.content, 'comment -> lt.comment).executeInsert()
      lt.copy(id = id)
    } { id =>
      SQL("""UPDATE latin_text
         | SET title = {title},
         | content = {content},
         | comment = {comment}
         | WHERE id = {id}
         |""".stripMargin).on('id -> id, 'title -> lt.title, 'content -> lt.content, 'comment -> lt.comment).executeUpdate()
      lt
    }
  }

  def load(id: Long) = DB.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment
      |   FROM latin_text
      |   WHERE id = {id}
      |""".stripMargin).on('id -> id).executeQuery().as(parser.single)
  }

  def fetchAll = DB.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment
      |   FROM latin_text
      |""".stripMargin).executeQuery().as(parser.*)
  }
}
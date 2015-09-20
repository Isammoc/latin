package services

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

import models.LatinText

@javax.inject.Singleton
class LatinTextManager {
  val parser = for {
    id <- long("id")
    title <- str("title")
    content <- str("content")
    comment <- str("comment")
    public <- bool("public")
  } yield LatinText(Some(id), title, content, comment, public)

  def save(lt: LatinText) = DB.withConnection {implicit c =>
    lt.id.fold {
      val id = SQL("""INSERT INTO latin_text (title, content, comment, public) VALUES ({title}, {content}, {comment}, {public})""").on('title -> lt.title, 'content -> lt.content, 'comment -> lt.comment, 'public -> lt.public).executeInsert()
      lt.copy(id = id)
    } { id =>
      SQL("""UPDATE latin_text
         | SET title = {title},
         | content = {content},
         | comment = {comment},
         | public = {public}
         | WHERE id = {id}
         |""".stripMargin).on('id -> id, 'title -> lt.title, 'content -> lt.content, 'comment -> lt.comment, 'public -> lt.public).executeUpdate()
      lt
    }
  }

  def load(id: Long) = DB.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   WHERE id = {id}
      |""".stripMargin).on('id -> id).executeQuery().as(parser.single)
  }

  def fetchAll = DB.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   ORDER BY id DESC
      |""".stripMargin).executeQuery().as(parser.*)
  }

  def fetchPublic = DB.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   WHERE public IS TRUE
      |   ORDER BY id DESC
      |""".stripMargin).executeQuery().as(parser.*)
  }

  def delete(id: Long) = DB.withConnection {implicit c =>
    SQL("""DELETE FROM latin_text WHERE id = {id}""").on('id -> id).execute()
  }

}
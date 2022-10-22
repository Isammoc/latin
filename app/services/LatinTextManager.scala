package services

import anorm._
import anorm.SqlParser._
import models.LatinText
import services.LatinTextManager.{decodeBoolean, encodeBoolean}

object LatinTextManager {
  def encodeBoolean(bool: Boolean): Int = if (bool) 1 else 0
  def decodeBoolean(value: Int): Boolean = value != 0
}

@javax.inject.Singleton
class LatinTextManager @javax.inject.Inject() (val db: play.api.db.Database){
  val parser = for {
    id <- long("id")
    title <- str("title")
    content <- str("content")
    comment <- str("comment")
    public <- int("public")
  } yield LatinText(Some(id), title, content, comment, decodeBoolean(public))

  def save(lt: LatinText) = db.withConnection { implicit c =>
    lt.id.fold {
      val id = SQL("""INSERT INTO latin_text (title, content, comment, public) VALUES ({title}, {content}, {comment}, {public})""").on('title -> lt.title, 'content -> lt.content, 'comment -> lt.comment, 'public -> encodeBoolean(lt.public)).executeInsert()
      lt.copy(id = id)
    } { id =>
      SQL("""UPDATE latin_text
         | SET title = {title},
         | content = {content},
         | comment = {comment},
         | public = {public}
         | WHERE id = {id}
         |""".stripMargin).on('id -> id, 'title -> lt.title, 'content -> lt.content, 'comment -> lt.comment, 'public -> encodeBoolean(lt.public)).executeUpdate()
      lt
    }
  }

  def load(id: Long) = db.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   WHERE id = {id}
      |""".stripMargin).on('id -> id).executeQuery().as(parser.single)
  }

  def fetchAll = db.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   ORDER BY id DESC
      |""".stripMargin).executeQuery().as(parser.*)
  }

  def fetchPublic = db.withConnection { implicit c =>
    SQL("""
      | SELECT id, title, content, comment, public
      |   FROM latin_text
      |   WHERE public IS TRUE
      |   ORDER BY id DESC
      |""".stripMargin).executeQuery().as(parser.*)
  }

  def delete(id: Long) = db.withConnection { implicit c =>
    SQL("""DELETE FROM latin_text WHERE id = {id}""").on('id -> id).execute()
  }

}
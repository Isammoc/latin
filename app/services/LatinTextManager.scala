package services

import anorm._
import play.api.db.DB
import play.api.Play.current

import models.LatinText

object LatinTextManager {
  def save(lt: LatinText) = DB.withConnection {implicit c =>
    val id = SQL("""INSERT INTO latin_text (title, content, comment) VALUES ({title}, {content}, {comment})""").on('title -> lt.title, 'content -> lt.content, 'comment -> lt.comment).executeInsert()
    lt.copy(id = id)
  }
}
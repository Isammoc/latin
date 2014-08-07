package models

case class LatinText(id: Option[Long], title: String, content: String, comment: String) {
  def duplicate = this.copy(id = None)
}
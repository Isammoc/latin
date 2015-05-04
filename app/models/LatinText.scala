package models

case class LatinText(id: Option[Long], title: String, content: String, comment: String, public: Boolean = false) {
  def duplicate = this.copy(id = None)
}
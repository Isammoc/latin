package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.LatinText
import services.LatinTextManager

object Application extends Controller {
  val latinTextForm = Form(mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "comment" -> text
      )(LatinText.apply)(LatinText.unapply)
    )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def show(id: Long) = Action {
    Ok(views.html.show(LatinTextManager.load(id)))
  }

  def edit = Action {
    val data = latinTextForm.fill(LatinText(None, "Cicéron in Tusculanes III, II, 3-4", """<span data-type="inv">Ad</span> <span data-type="acc f sg">quam</span> <span data-type="indic prés P3 passif">fertur</span> <span data-type="nom masc sg">optimus quisque</span> <span data-type="acc f sg">veram<span data-type="inv">que</span> illam honestatem</span>
<span data-type="nom masc sg">expetens</span> <span data-type="acc f sg">quam unam</span> <span data-type="nom f sg">natura</span> <span data-type="inv">maxime</span> <span data-type="indic prés P3">anquirit</span>, <span data-type="inv">in</span> <span data-type="abl f sg">summa inanitate</span> <span data-type="indic prés P3 dép">versatur
consectatur<span data-type="inv">que</span></span> <span data-type="acc f sg">nullam eminentem effigiem</span> <span data-type="gén f sg">virtutis</span>, <span data-type="inv">sed</span> <span data-type="acc f sg">adumbratam imaginem</span> <span data-type="gén f sg">gloriae</span>.
""", ""))
    Ok(views.html.edit(data))
  }

  def save = Action { implicit request =>
    val form = latinTextForm.bindFromRequest()
    form.fold(
        formWithErrors => BadRequest(views.html.edit(formWithErrors)),
        {latinText =>
          val lt = LatinTextManager.save(latinText)
          Redirect(routes.Application.show(lt.id.get))})
  }
}
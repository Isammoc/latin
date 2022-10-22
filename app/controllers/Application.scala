package controllers

import actions.{LoggedAction, PublicAction}
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.LatinText

class Application @javax.inject.Inject() (
                                           val latinTextManager : services.LatinTextManager,
                                           cc: ControllerComponents,
                                           configuration: Configuration,
                                           publicAction: PublicAction,
                                           loggedAction: LoggedAction
                                         ) extends AbstractController(cc) {
  val latinTextForm = Form(mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "comment" -> text,
      "public" -> boolean
      )(LatinText.apply)(LatinText.unapply)
    )

  def index = publicAction { implicit request =>
    val latinTexts = if(request.authenticated) latinTextManager.fetchAll else latinTextManager.fetchPublic
    Ok(views.html.index(latinTexts))
  }

  def show(id: Long) = publicAction { implicit request =>
    val lt = latinTextManager.load(id)
    if(lt.public || request.authenticated) {
      Ok(views.html.show(latinTextManager.load(id)))
    } else {
      loggedAction.unauthorized(request)
    }
  }

  def edit(id: Long) = loggedAction { implicit request =>
    Ok(views.html.edit(latinTextForm.fill(latinTextManager.load(id))))
  }

  def create = loggedAction { implicit request =>
    val data = latinTextForm.fill(LatinText(None, "Cicéron in Tusculanes III, II, 3-4", """<span data-type="inv">Ad</span> <span data-type="acc f sg">quam</span> <span data-type="indic prés P3 passif">fertur</span> <span data-type="nom masc sg">optimus quisque</span> <span data-type="acc f sg">veram<span data-type="inv">que</span> illam honestatem</span>
<span data-type="nom masc sg">expetens</span> <span data-type="acc f sg">quam unam</span> <span data-type="nom f sg">natura</span> <span data-type="inv">maxime</span> <span data-type="indic prés P3">anquirit</span>, <span data-type="inv">in</span> <span data-type="abl f sg">summa inanitate</span> <span data-type="indic prés P3 dép">versatur
consectatur<span data-type="inv">que</span></span> <span data-type="acc f sg">nullam eminentem effigiem</span> <span data-type="gén f sg">virtutis</span>, <span data-type="inv">sed</span> <span data-type="acc f sg">adumbratam imaginem</span> <span data-type="gén f sg">gloriae</span>.
""", ""))
    Ok(views.html.edit(data))
  }

  def save = loggedAction { implicit request =>
    val form = latinTextForm.bindFromRequest()
    form.fold(
        formWithErrors => BadRequest(views.html.edit(formWithErrors)),
        {latinText =>
          val lt = latinTextManager.save(latinText)
          Redirect(routes.Application.show(lt.id.get))})
  }

  def delete(id: Long) = loggedAction { implicit request =>
    latinTextManager.delete(id)
    Redirect(routes.Application.index)
  }

  def help = Action { implicit request =>
    Ok(views.html.help())
  }
}
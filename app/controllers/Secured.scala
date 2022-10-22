package controllers

import actions.LoggedAction
import play.api.mvc._
import play.api._

import javax.inject.Inject

class UserRequest[A](val authenticated: Boolean, request: Request[A]) extends WrappedRequest[A](request)

class Secured @Inject() (cc: ControllerComponents, configuration: Configuration) extends AbstractController(cc) {
  def logout: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.Application.index).withSession(request.session - "pass")
  }

  def authenticate: Action[AnyContent] = Action { implicit request =>
    LoggedAction.authenticateForm(configuration).bindFromRequest().fold(
      form => BadRequest(views.html.authenticate(form)),
      pass => Redirect(routes.Application.index).withSession("pass" -> pass))
  }
}

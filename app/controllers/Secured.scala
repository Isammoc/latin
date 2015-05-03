package controllers

import play.api.mvc.Controller
import play.api.data.Form
import play.api.mvc.Action
import play.api.data.Forms._
import play.api.mvc._
import play.api._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserRequest[A](val authenticated: Boolean, request: Request[A]) extends WrappedRequest[A](request)

object Secured extends Controller {
  lazy val password = Play.current.configuration.getString("application.password").getOrElse("toto")

  trait Authenticated;

  val authenticateForm = Form(single("pass" -> nonEmptyText.verifying("Ce n'est pas le bon mot de passe",_ == password)))

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withSession(request.session - "pass")
  }

  def authenticate = Action { implicit request =>
    authenticateForm.bindFromRequest().fold(
      form => BadRequest(views.html.authenticate(form)),
      _ => Redirect(routes.Application.index)).withSession("pass" -> password)
  }

  object LoggingAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      request.session.get("pass").flatMap(pass => if (pass == password) Some(pass) else None) match {
        case None => Future.successful(Ok(views.html.authenticate(authenticateForm)(new UserRequest(false, request))))
        case _ => block(new UserRequest(true, request))
      }
    }
  }
}
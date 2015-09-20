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

class Secured extends Controller {

  trait Authenticated;

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withSession(request.session - "pass")
  }

  def authenticate = Action { implicit request =>
    Secured.authenticateForm.bindFromRequest().fold(
      form => BadRequest(views.html.authenticate(form)),
      _ => Redirect(routes.Application.index)).withSession("pass" -> Secured.password)
  }

}

object Secured {
  lazy val password = Play.current.configuration.getString("application.password").getOrElse("toto")
  val authenticateForm = Form(single("pass" -> nonEmptyText.verifying("Ce n'est pas le bon mot de passe",_ == password)))
  def unauthorized[A](userRequest: UserRequest[A]) = Unauthorized(views.html.authenticate(authenticateForm)(userRequest))
  object LoggingAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      request.session.get("pass").flatMap(pass => if (pass == password) Some(pass) else None) match {
        case None => Future.successful(unauthorized(new UserRequest(false, request)))
        case _ => block(new UserRequest(true, request))
      }
    }
  }

  object PublicAction extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]) = {
      block(new UserRequest(request.session.get("pass").flatMap(pass => if (pass == password) Some(pass) else None) != None, request))
    }
  }
}
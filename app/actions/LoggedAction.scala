package actions

import controllers.UserRequest
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc.Results.Unauthorized
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

object LoggedAction {
  def password(configuration: Configuration): String = configuration.getOptional[String]("application.password").getOrElse("toto")

  def authenticateForm(password: String): Form[String] = Form(single("pass" -> nonEmptyText.verifying("Ce n'est pas le bon mot de passe", _ == password)))

  def authenticateForm(configuration: Configuration): Form[String] = authenticateForm(password(configuration))
}

class LoggedAction @Inject() (parser: BodyParsers.Default, configuration: Configuration)(implicit executionContext: ExecutionContext) extends ActionBuilderImpl(parser) {
  def unauthorized[A](userRequest: UserRequest[A]) = Unauthorized(views.html.authenticate(LoggedAction.authenticateForm(configuration))(userRequest))

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    request.session.get("pass").flatMap(pass => if (pass == LoggedAction.password(configuration)) Some(pass) else None) match {
      case None => Future.successful(unauthorized(new UserRequest(false, request)))
      case _ => block(new UserRequest(true, request))
    }
  }
}

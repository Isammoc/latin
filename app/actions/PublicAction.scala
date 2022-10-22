package actions

import controllers.UserRequest
import play.api._
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PublicAction @Inject() (configuration: Configuration, val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  val password = configuration.getOptional[String]("application.password").getOrElse("toto")

  override def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
    block(new UserRequest(request.session.get("pass").flatMap(pass => if (pass == password) Some(pass) else None) != None, request))
  }
}

package controllers

import akka.NotUsed
import akka.stream.scaladsl.Source

import javax.inject._
import play.api._
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.mvc._
import services.FakeEventSender.sendFakeAlerts
import services.{SSE_SessionHandling, SessionId, ThresholdsAlert}

import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the application's home page.
 */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method will be called when the application receives a `GET`
   * request with a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def testSSE(sessionId: String) = Action {
    val source: Source[String, NotUsed] =
      SSE_SessionHandling.createSourceBySessionId(SessionId(sessionId)).map(x => format(x.numberOfAlerts.toString))


    Results.Ok
      .chunked(source via EventSource.flow)
      .as(ContentTypes.EVENT_STREAM)
      .withHeaders("Cache-Control" -> "no-cache")
      .withHeaders("Connection" -> "keep-alive")
  }

  def start(sessionId: String) = Action.async {
    sendFakeAlerts(SessionId(sessionId)).map { _ =>
      Results.Ok
    }.recover{
      case _: Throwable =>
        Results.InternalServerError
    }
  }

  private def format(message: String): String = {
    s"$message\n\n"
  }

}

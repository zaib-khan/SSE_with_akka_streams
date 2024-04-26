package services

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.QueueOfferResult.{ Dropped, Enqueued, Failure, QueueClosed }
import akka.stream.scaladsl.{ Source, SourceQueueWithComplete }
import akka.stream.{ Materializer, OverflowStrategy }

import scala.collection.mutable
import scala.concurrent.Future

object SSE_SessionHandling {
  implicit private val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit private val system: ActorSystem = ActorSystem("SSE_TEST")
  implicit val materializer: Materializer = Materializer(system)

  private val sourcePerSession = mutable.HashMap.empty[SessionId, Source[ThresholdsAlert, NotUsed]]
  private val sourceQueuePerSession = mutable.HashMap.empty[SessionId, SourceQueueWithComplete[ThresholdsAlert]]


  def createSourceBySessionId(sessionId: SessionId): Source[ThresholdsAlert, NotUsed] = {
    val initialSource = Source.queue[ThresholdsAlert](100, OverflowStrategy.dropHead)

    val sourceQueueOfThresholdsAlert: (SourceQueueWithComplete[ThresholdsAlert], Source[ThresholdsAlert, NotUsed]) =
      initialSource.preMaterialize()

    sourcePerSession.update(sessionId, sourceQueueOfThresholdsAlert._2)
    sourceQueuePerSession.update(sessionId, sourceQueueOfThresholdsAlert._1)
    sourceQueueOfThresholdsAlert._2
  }

  def registreNewAlert(sessionId: SessionId, alert: ThresholdsAlert) : Future[Unit] = {
    val sourceQueueOpt = sourceQueuePerSession.get(sessionId)

    sourceQueueOpt match {
      case Some(sourceQueue) =>
        sourceQueue.offer(alert).flatMap{
          case Enqueued => Future.successful(())
          case error => Future.failed(new Exception(s"Error -> $error"))
        }
      case None => Future.failed(new Exception(s"The source has been closed for $sessionId"))
    }

  }














}

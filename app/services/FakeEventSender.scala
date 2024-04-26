package services

import services.SSE_SessionHandling.registreNewAlert

object FakeEventSender {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val rand = new scala.util.Random


  def sendFakeAlerts(sessionId: SessionId) = {
    Thread.sleep(1000)
    for{
      _ <- registreNewAlert(sessionId,ThresholdsAlert(rand.between(1,20)))
      _ = Thread.sleep(2000)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(1000)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(3500)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(500)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(6000)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(9000)
      _ <- registreNewAlert(sessionId, ThresholdsAlert(rand.between(1, 20)))
      _ = Thread.sleep(1000)
    } yield ()





  }


















}

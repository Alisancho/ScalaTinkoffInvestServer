package ru.invest

import java.util.logging.Logger

import akka.actor.ActorSystem
import cats.effect.{ExitCode, IO, IOApp}
import ru.invest.core.config.ConfigObject.TOKEN
import ru.invest.service.MonitoringServiceImpl

import scala.concurrent.ExecutionContextExecutor

object AppStart extends IOApp {

  private val logger = Logger.getLogger("Pooo")
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val ec: ExecutionContextExecutor = system.dispatcher

  override def run(args: List[String]): IO[ExitCode] =
    for {
      l <- IO { new MonitoringServiceImpl(TOKEN, logger) }
//      _ <- Http().bindAndHandle()
      _ = l.startNewMonitoring("BBG004730N88")
      _ = l.startNewMonitoring("BBG000BCVJ77")
      _ = l.startNewMonitoring("BBG000BJ5HK0")
      _ = l.startNewMonitoring("BBG000BR14K5")
      _ = l.startNewMonitoring("BBG000NV1KK7")
    } yield ExitCode.Success
}

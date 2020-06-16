package ru.invest

import java.util.logging.Logger

import cats.effect.ExitCode
import ru.invest.core.config.ConfigObject._
import ru.invest.service._

import scala.language.postfixOps
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.controllers.TaskController
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      api <- apiTask
      ts  <- Task { new TinkoffRESTServiceImpl(api, TINKOFF_BROKER_ACCOUNT_ID) }
      ta  = system.actorOf(TelegramActorMess(schedulerTinkoff))
      tel <- startTelegramService(ta)
      bu  <- Task { new BusinessProcessServiceImpl(ts)(schedulerTinkoff)(materialiver) }
      ggg = System.getenv()
      _   = ta ! bu
//      tc  <- Task { new TaskController() }
//      _   <- Task.fromFuture { Http().bindAndHandle(Route.handlerFlow(tc.routApiV1()), SERVER_HOST, ggg.get("PORT").toInt) }
    } yield ExitCode.Success
}

trait AppStartHelper extends LazyLogging {
  implicit val system: ActorSystem          = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val materialiver: Materializer   = ActorMaterializer()
  implicit val schedulerTinkoff: SchedulerService =
    Scheduler.fixedPool(name = "my-fixed-tinkoff", poolSize = SCHEDULER_POOL_TINKOFF)

  val apiTask: Task[OpenApi] = for {
    log <- Task { Logger.getLogger("Pooo") }
    api <- Task { new OkHttpOpenApiFactory(TOKEN, log).createOpenApiClient(schedulerTinkoff) }
  } yield api

  def proxyLogic[T](q: Boolean, w: T): Option[T] =
    if (q) {
      Option.apply(w)
    } else {
      Option.empty
    }

  val startTelegramService: ActorRef => Task[TelegramServiceImpl] = actor =>
    Task {
      new TelegramServiceImpl(TELEGRAM_TOKEN,
                              TELEGRAM_NAMEBOT,
                              TELEGRAM_CHAT_ID,
                              actor,
                              proxyLogic(TELEGRAM_PROXY, TELEGRAM_HOST),
                              proxyLogic(TELEGRAM_PROXY, TELEGRAM_PORT))
  }

}

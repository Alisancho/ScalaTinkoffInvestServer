package ru.invest

import java.util.concurrent.Executors
import java.util.logging.Logger

import akka.http.scaladsl.Http
import cats.effect.ExitCode
import ru.invest.core.config.ConfigObject._
import ru.invest.service.{
  BusinessProcessServiceImpl,
  DataBaseServiceImpl,
  MonitoringServiceImpl,
  TelegramActorMess,
  TelegramServiceImpl,
  TinkoffRESTServiceImpl
}

import scala.language.postfixOps
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.controllers.TaskController
import ru.invest.core.config.MyContext
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      api <- apiTask
      ts  <- Task { new TinkoffRESTServiceImpl(api, TINKOFF_BROKER_ACCOUNT_ID) }
      ms  <- Task { new MonitoringServiceImpl(api) }
      ta  = system.actorOf(TelegramActorMess(ms)(schedulerTinkoff))
      _   <- startTelegramService(ta)
      dbs <- Task { new DataBaseServiceImpl }
      bu  <- Task { new BusinessProcessServiceImpl(ts, dbs, ms)(schedulerDB, schedulerTinkoff) }
//      tc  <- Task { new TaskController(bu)(schedulerTinkoff) }
//      _   <- Task.fromFuture { Http().bindAndHandle(tc.routApiV1, SERVER_HOST, SERVER_PORT) }
  //    _   <- bu.ubdateTinkoffToolsTable
      _   = bu.startAllTaskMonitoring().runAsyncAndForget(schedulerDB)
//    _ <- Task(ms.monitorGraph.run()).onErrorHandle(o => logger.error(o.getMessage))
    } yield ExitCode.Success
}

trait AppStartHelper extends LazyLogging {
  implicit val system: ActorSystem          = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val ctx: MyContext               = new MyContext()
  val schedulerDB: SchedulerService         = Scheduler.fixedPool(name = "my-fixed-db", poolSize = SCHEDULER_POOL_DB)
  val schedulerTinkoff: SchedulerService    = Scheduler.fixedPool(name = "my-fixed-tinkoff", poolSize = SCHEDULER_POOL_TINKOFF)

  val apiTask: Task[OpenApi] = for {
    log <- Task { Logger.getLogger("Pooo") }
    api <- Task { new OkHttpOpenApiFactory(TOKEN, log).createOpenApiClient(Executors.newFixedThreadPool(POOL_OPENAPI)) }
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

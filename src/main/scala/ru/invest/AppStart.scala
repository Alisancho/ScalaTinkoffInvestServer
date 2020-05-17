package ru.invest

import java.util.logging.Logger

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
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.core.config.MyContext
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      api <- apiTask
      dbs <- Task { new DataBaseServiceImpl }
      ts  <- Task { new TinkoffRESTServiceImpl(api, TINKOFF_BROKER_ACCOUNT_ID) }
      ms  <- Task { new MonitoringServiceImpl(api)(schedulerDB) }
      ta  = system.actorOf(TelegramActorMess(ms, dbs)(schedulerTinkoff, schedulerDB))
      tel <- startTelegramService(ta)
      bu  <- Task { new BusinessProcessServiceImpl(ts, dbs, ms, tel)(schedulerDB, schedulerTinkoff, materialiver) }
//      tc  <- Task { new TaskController(bu)(schedulerTinkoff) }
//      _   <- Task.fromFuture { Http().bindAndHandle(tc.routApiV1, SERVER_HOST, SERVER_PORT) }
      _ <- bu.ubdateTinkoffToolsTable
      _ <- bu.updateTaskMonitoringTbl
      _ = bu.startAllTaskMonitoring.runAsyncAndForget(schedulerDB)
    } yield ExitCode.Success
}

trait AppStartHelper extends LazyLogging {
  implicit val system: ActorSystem          = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val materialiver: Materializer   = ActorMaterializer()
  implicit val ctx: MyContext               = new MyContext()
  val schedulerDB: SchedulerService         = Scheduler.fixedPool(name = "my-fixed-db", poolSize = SCHEDULER_POOL_DB)
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

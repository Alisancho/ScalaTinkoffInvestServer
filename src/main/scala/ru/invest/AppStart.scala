package ru.invest

import java.util.logging.Logger

import akka.http.scaladsl.Http
import cats.effect.ExitCode
import ru.invest.core.config.ConfigObject.{SERVER_HOST, SERVER_PORT, TINKOFF_BROKER_ACCOUNT_ID, TOKEN}
import ru.invest.service.{BusinessProcessServiceImpl, DataBaseServiceImpl, TinkoffRESTServiceImpl}

import scala.language.postfixOps
import akka.actor.ActorSystem
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.controllers.TaskController
import ru.invest.core.config.MyContext

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      ts  <- Task { new TinkoffRESTServiceImpl(TOKEN, TINKOFF_BROKER_ACCOUNT_ID, loggerForStream) }
      dbs <- Task { new DataBaseServiceImpl }
      bu  <- Task { new BusinessProcessServiceImpl(ts, dbs)(schedulerDB, schedulerTinkoff) }
      tc  <- Task { new TaskController(bu)(schedulerTinkoff) }
      _   <- Task.fromFuture { Http().bindAndHandle(tc.routApiV1, SERVER_HOST, SERVER_PORT) }
      _   <- bu.ubdateTinkoffToolsTable
      _   <- bu.startMonitoringMyProfil
    } yield ExitCode.Success
}

trait AppStartHelper {
  implicit val system: ActorSystem          = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val ctx: MyContext               = new MyContext()
  val schedulerDB: SchedulerService         = Scheduler.fixedPool(name = "my-fixed", poolSize = 4)
  val schedulerTinkoff: SchedulerService    = Scheduler.fixedPool(name = "my-fixed", poolSize = 4)
  val loggerForStream: Logger               = Logger.getLogger("Pooo")
}

package ru.invest

import java.time.Instant
import java.util.Date
import java.util.logging.Logger

import akka.http.scaladsl.Http
import cats.effect.ExitCode
import ru.invest.core.config.ConfigObject.{SERVER_HOST, SERVER_PORT, TINKOFF_BROKER_ACCOUNT_ID, TOKEN}
import ru.invest.service.{BusinessProcessServiceImpl, DataBaseServiceImpl, TinkoffRESTServiceImpl}

import scala.language.postfixOps
import akka.actor.ActorSystem
import io.getquill._
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.controllers.TaskController

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      ts  <- Task { new TinkoffRESTServiceImpl(TOKEN, TINKOFF_BROKER_ACCOUNT_ID, logger) }
      dbs <- Task { new DataBaseServiceImpl }
      tc  <- Task { new TaskController(ts, dbs) }
      bu  <- Task { new BusinessProcessServiceImpl(ts, dbs)(schedulerDB) }
      _   <- Task.fromFuture { Http().bindAndHandle(tc.routApiV1, SERVER_HOST, SERVER_PORT) }
      k   <- bu.ubdateTaskMonitorind
//      d = k.instruments.stream().forEach(p => cont.setBlackListFriend(p).runAsyncAndForget(schedulerTask))
    } yield ExitCode.Success
}

trait AppStartHelper {
  implicit val system: ActorSystem            = ActorSystem()
  implicit val ec: ExecutionContextExecutor   = system.dispatcher
  implicit val ctx: MyContext                 = new MyContext()
  lazy val schedulerDB: SchedulerService      = Scheduler.fixedPool(name = "my-fixed", poolSize = 4)
  lazy val schedulerTinkoff: SchedulerService = Scheduler.fixedPool(name = "my-fixed", poolSize = 4)
  val logger: Logger                          = Logger.getLogger("Pooo")

}

class MyContext extends MysqlMonixJdbcContext(SnakeCase, "ctx") {
  lazy implicit val instantEncoder = MappedEncoding[Instant, Date] { i =>
    new Date(i.toEpochMilli)
  }
  lazy implicit val instantDecoder = MappedEncoding[Date, Instant] { d =>
    Instant.ofEpochMilli(d.getTime)
  }
}

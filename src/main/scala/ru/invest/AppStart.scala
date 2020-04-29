package ru.invest

import java.time.Instant
import java.util.Date
import java.util.logging.Logger

import akka.http.scaladsl.Http
import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import ru.invest.core.config.ConfigObject.{SERVER_HOST, SERVER_PORT, TOKEN, Token}
import ru.invest.service.{DataBaseServiceImpl, TinkoffRESTServiceImpl}

import scala.language.postfixOps
import akka.actor.ActorSystem
import com.softwaremill.macwire.wire
import com.softwaremill.tagging.@@
import io.getquill._
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import ru.invest.controllers.TaskController

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps

object AppStart extends TaskApp with AppStartHelper {

  override def run(args: List[String]): Task[ExitCode] =
    for {
      ts  <- Task { new TinkoffRESTServiceImpl(TOKEN, logger) }
      dbs <- Task { new DataBaseServiceImpl }
      tc  <- Task { new TaskController(ts, dbs) }
      _   <- Task.fromFuture { Http().bindAndHandle(tc.routApiV1, SERVER_HOST, SERVER_PORT) }
//      k <- l.getMarketStocks
//      d = k.instruments.stream().forEach(p => cont.setBlackListFriend(p).runAsyncAndForget(schedulerTask))
    } yield ExitCode.Success
}

trait AppStartHelper {
  implicit val system: ActorSystem          = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val ctx: MyContext               = new MyContext()
  lazy val schedulerTask: SchedulerService  = Scheduler.fixedPool(name = "my-fixed", poolSize = 2)
  val logger: Logger                        = Logger.getLogger("Pooo")
}

class MyContext extends MysqlMonixJdbcContext(SnakeCase, "ctx") {
  lazy implicit val instantEncoder = MappedEncoding[Instant, Date] { i =>
    new Date(i.toEpochMilli)
  }
  lazy implicit val instantDecoder = MappedEncoding[Date, Instant] { d =>
    Instant.ofEpochMilli(d.getTime)
  }
}
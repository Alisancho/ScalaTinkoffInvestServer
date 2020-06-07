package ru.invest.service
import akka.NotUsed
import akka.stream.{Materializer, SharedKillSwitch}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.analysis._
import ru.invest.entity.database.AnalyticsTbl
import akka.util.ccompat.JavaConverters._

import scala.language.postfixOps
import scala.concurrent.duration._

class BusinessProcessServiceImpl(tinkoffRESTServiceImpl:TinkoffRESTServiceImpl)(sheduler: SchedulerService)(materializer:Materializer)
    extends LazyLogging {

  def startAnalyticsJob(sharedKillSwitch: SharedKillSwitch)(f: AnalyticsTbl => Task[_]): Task[Unit] =
    for {
      c    <- tinkoffRESTServiceImpl.getMarketStocks
      list = c.instruments.asScala.toList.map(_.figi)
      _    = analyticsStream(list, sharedKillSwitch)(tinkoffRESTServiceImpl)(f)(sheduler).run()(materializer)
    } yield ()

   def analyticsStream(list: List[String], sharedKillSwitch: SharedKillSwitch)
                      (tinkoff: TinkoffRESTServiceImpl)
                      (f: AnalyticsTbl => Task[_])
                      (sheduler: SchedulerService): RunnableGraph[NotUsed] =
    Source(list)
      .throttle(1, 800.millis)
      .via(sharedKillSwitch.flow)
      .map(figi => {
        tinkoff
          .getMarketCandles(figi)
          .runAsync {
            case Left(value) => logger.error(value.getMessage)
            case Right(value) => {
              val list = value.get()
              logger.info(list.toString)
              list.toAbsorption(f)(sheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(sheduler)
              list.toHammer(f)(sheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(sheduler)
              list.toHarami(f)(sheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(sheduler)
            }
          }(sheduler)
      })
      .to(Sink.ignore)

}

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
import ru.tinkoff.invest.openapi.models.market.Instrument

import scala.language.postfixOps
import scala.concurrent.duration._

class BusinessProcessServiceImpl(tinkoffRESTServiceImpl:TinkoffRESTServiceImpl)(scheduler: SchedulerService)(materializer:Materializer)
    extends LazyLogging {

  def startAnalyticsJob(sharedKillSwitch: SharedKillSwitch)(f: String => Task[_]): Task[Unit] =
    for {
      c    <- tinkoffRESTServiceImpl.getMarketStocks
      list = c.instruments.asScala.toList
      _    = analyticsStream(list, sharedKillSwitch)(tinkoffRESTServiceImpl)(f)(scheduler).run()(materializer)
    } yield ()

   def analyticsStream(list: List[Instrument], sharedKillSwitch: SharedKillSwitch)
                      (tinkoff: TinkoffRESTServiceImpl)
                      (f: String => Task[_])
                      (scheduler: SchedulerService): RunnableGraph[NotUsed] =
    Source(list)
      .throttle(1, 800.millis)
      .via(sharedKillSwitch.flow)
      .map(instrument => {
        tinkoff
          .getMarketCandles(instrument.figi)
          .runAsync {
            case Left(value) => logger.error(value.getMessage)
            case Right(value) => {
              val list = value.get()
              logger.info(list.toString)
              list.toAbsorption(f)(instrument)(scheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(scheduler)
              list.toHammer(f)(instrument)(scheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(scheduler)
              list.toHarami(f)(instrument)(scheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(scheduler)
            }
          }(scheduler)
      })
      .to(Sink.ignore)


  val logError:Either[Throwable,String] => Unit = {
    case Right(error) => {}
    case Left(x) => {}
  }

}

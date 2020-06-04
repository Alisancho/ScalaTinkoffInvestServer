package ru.invest.service
import akka.{Done, NotUsed}
import akka.actor.ActorRef
import akka.stream.{ActorMaterializer, Materializer, SharedKillSwitch, ThrottleMode}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.analysis._

import scala.language.postfixOps
import akka.util.ccompat.JavaConverters._

import scala.concurrent.duration._

class BusinessProcessServiceImpl(tinkoffRESTServiceImpl: TinkoffRESTServiceImpl,
                                 dataBaseServiceImpl: DataBaseServiceImpl,
                                 monitoringServiceImpl: MonitoringServiceImpl,
                                 telegramServiceImpl: TelegramServiceImpl)(schedulerDB: SchedulerService,
                                                                           schedulerTinkoff: SchedulerService,
                                                                           materializer: Materializer)
    extends LazyLogging {

  def startAllTaskMonitoring: Task[_] =
    (for {
      z  <- dataBaseServiceImpl.selectTaskMonitoring
      ll = MVarServiceImpl(z)
      _  = z.foreach(o => monitoringServiceImpl.startMonitoring(o.figi).runAsyncAndForget(schedulerTinkoff))
      _  = monitoringServiceImpl.mainStream(ll, telegramServiceImpl).run()(materializer)
    } yield ()).onErrorHandle(p => {
      logger.error(p.getMessage)
    })

  def ubdateTinkoffToolsTable: Task[_] =
    for {
      mc <- tinkoffRESTServiceImpl.getMarketCurrencies
      mb <- tinkoffRESTServiceImpl.getMarketBonds
      me <- tinkoffRESTServiceImpl.getMarketEtfs
      ms <- tinkoffRESTServiceImpl.getMarketStocks
      _  = mc.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = mb.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = me.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = ms.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
    } yield ()

  def updateTaskMonitoringTbl: Task[_] =
    for {
      mc <- tinkoffRESTServiceImpl.getPortfolio
      _ = mc.positions
        .stream()
        .forEach(o => dataBaseServiceImpl.insertTaskMonitoringTbl(o).runAsyncAndForget(schedulerDB: SchedulerService))
    } yield ()

  def startAnalyticsJob(sharedKillSwitch: SharedKillSwitch): Task[Unit] =
    for {
      c    <- tinkoffRESTServiceImpl.getMarketStocks
      list = c.instruments.asScala.toList.map(_.figi)
      _    = analyticsStream(list, sharedKillSwitch)(tinkoffRESTServiceImpl, dataBaseServiceImpl)(schedulerDB).run()(materializer)
    } yield ()

  private def analyticsStream(list: List[String], sharedKillSwitch: SharedKillSwitch)(
      tinkoff: TinkoffRESTServiceImpl,
      dbs: DataBaseServiceImpl)(sheduler: SchedulerService): RunnableGraph[NotUsed] =
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
              list.toAbsorption(dbs.insertAnalyticsTblTbl)(sheduler).runAsyncAndForget(sheduler)
              list.toHammer(dbs.insertAnalyticsTblTbl)(sheduler).runAsyncAndForget(sheduler)
              list.toHarami(dbs.insertAnalyticsTblTbl)(sheduler).runAsyncAndForget(sheduler)
            }
          }(sheduler)
      })
      .to(Sink.ignore)

}

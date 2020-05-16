package ru.invest.service
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging

import monix.eval.Task
import monix.execution.schedulers.SchedulerService


class BusinessProcessServiceImpl(tinkoffRESTServiceImpl: TinkoffRESTServiceImpl,
                                 dataBaseServiceImpl: DataBaseServiceImpl,
                                 monitoringServiceImpl: MonitoringServiceImpl,
                                 telegramServiceImpl: TelegramServiceImpl)(schedulerDB: SchedulerService,
                                                                           schedulerTinkoff: SchedulerService,
                                                                           materializer: Materializer)
    extends LazyLogging {

  def startAllTaskMonitoring(): Task[String] =
    (for {
      z  <- dataBaseServiceImpl.selectTaskMonitoring
      ll = MVarServiceImpl(z)
      _  = z.foreach(o => monitoringServiceImpl.startMonitoring(o.figi))
      _  = monitoringServiceImpl.mainStream(ll, telegramServiceImpl).run()(materializer)
    } yield "OK").onErrorHandle(p => {
      logger.error(p.getMessage)
      "NOT"
    })

  def ubdateTinkoffToolsTable: Task[Boolean] =
    for {
      mc <- tinkoffRESTServiceImpl.getMarketCurrencies
      mb <- tinkoffRESTServiceImpl.getMarketBonds
      me <- tinkoffRESTServiceImpl.getMarketEtfs
      ms <- tinkoffRESTServiceImpl.getMarketStocks
      _  = mc.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = mb.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = me.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
      _  = ms.instruments.stream().forEach(m => dataBaseServiceImpl.insertTinkoffTools(m).runAsyncAndForget(schedulerDB))
    } yield true

  def updateTaskMonitoringTbl(): Task[Boolean] =
    for{
      mc <- tinkoffRESTServiceImpl.getPortfolio
    _ = mc.positions.stream().forEach(o => dataBaseServiceImpl.insertTaskMonitoringTbl(o).runAsyncAndForget(schedulerDB))
    }yield true

}

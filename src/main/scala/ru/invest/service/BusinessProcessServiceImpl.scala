package ru.invest.service
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.schedulers.SchedulerService

class BusinessProcessServiceImpl(
    tinkoffRESTServiceImpl: TinkoffRESTServiceImpl,
    dataBaseServiceImpl: DataBaseServiceImpl,
    monitoringServiceImpl: MonitoringServiceImpl)(schedulerDB: SchedulerService, schedulerTinkoff: SchedulerService)
    extends LazyLogging {

  def startAllTaskMonitoring(): Task[Boolean] =
    (for {
      z <- dataBaseServiceImpl.selectTaskMonitoring
      b = z.foreach(o => monitoringServiceImpl.startMonitoring(o.figi))
    } yield true).onErrorHandle(p => {
      logger.error(p.getMessage)
      false
    })

  def statrMonitoring: Task[String] =
    for {
      k <- dataBaseServiceImpl.selectFIGIMonitoring
      _ = k.foreach(w => monitoringServiceImpl.startMonitoring(w.figi))
    } yield "OK"

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

  def startMonitoringMyProfil: Task[Boolean] =
    for {
      q <- tinkoffRESTServiceImpl.getPortfolio
      _ = q.positions
        .stream()
        .forEach(p => monitoringServiceImpl.startMonitoring(p.figi))
    } yield true
}

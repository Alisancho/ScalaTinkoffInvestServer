package ru.invest.service
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
class BusinessProcessServiceImpl(tinkoffRESTServiceImpl: TinkoffRESTServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl)(
    schedulerDB: SchedulerService,schedulerTinkoff: SchedulerService) {

  def statrMonitoring: Task[String] =
    for {
      k <- dataBaseServiceImpl.selectFIGIMonitoring
      _ = k.foreach(w => tinkoffRESTServiceImpl.startNewMonitoring(w.figi).runAsync(_ => ())(schedulerTinkoff))
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
}

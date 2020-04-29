package ru.invest.service
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
class BusinessProcessServiceImpl(tinkoffRESTServiceImpl: TinkoffRESTServiceImpl,
                                 dataBaseServiceImpl: DataBaseServiceImpl)(schedulerDB: SchedulerService) {

  def ubdateTaskMonitorind: Task[Boolean] =
    for {
      q <- tinkoffRESTServiceImpl.getPortfolio
      _ = q.positions.stream().forEach(m => dataBaseServiceImpl.insertTaskMonitoring(m).runAsyncAndForget(schedulerDB))
    } yield true
}

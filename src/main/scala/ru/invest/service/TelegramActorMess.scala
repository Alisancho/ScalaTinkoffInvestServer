package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import monix.execution.schedulers.SchedulerService

object TelegramActorMess {
  def apply(monitoringServiceImpl: MonitoringServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl)(
      schedulerTinkoff: SchedulerService,
      schedulerDB: SchedulerService): Props =
    Props(new TelegramActorMess(monitoringServiceImpl, dataBaseServiceImpl)(schedulerTinkoff, schedulerDB))
}

class TelegramActorMess(monitoringServiceImpl: MonitoringServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl)(
    schedulerTinkoff: SchedulerService,
    schedulerDB: SchedulerService)
    extends Actor {
  val log: LoggingAdapter = Logging(context.system, this)
  def receive: Receive = {
    case a: String => {
      log.info("NEW_MESSEND_FROM_TELEGRAM=" + a)
      parsString(a)
    }
    case error => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsString(s: String): Unit = s match {
    case s if s.startsWith("/start") =>
      (for {
        p <- dataBaseServiceImpl.selectFIGIFromTicker(s.replace("/start ", ""))
        _ <- monitoringServiceImpl.startMonitoring(p)
      } yield ()).onErrorHandle(o => log.error(o.getMessage)).runAsyncAndForget(schedulerDB)
    case s if s.startsWith("/stop") => {
      monitoringServiceImpl.stopMonitoring(s.replace("/stop ", "")).runAsyncAndForget(schedulerTinkoff)
    }
    case s if s.startsWith("/help") => {}
    case s if s.startsWith("/log")  => {}
    case _                          => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }

}

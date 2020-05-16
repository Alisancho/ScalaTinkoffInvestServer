package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import monix.execution.schedulers.SchedulerService
import ru.invest.service.helpers.database.TinkoffToolsTbl
import ru.mytelegrambot.InvestInfoBot

object TelegramActorMess {
  def apply(monitoringServiceImpl: MonitoringServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl)(
      schedulerTinkoff: SchedulerService,
      schedulerDB: SchedulerService): Props =
    Props(new TelegramActorMess(monitoringServiceImpl, dataBaseServiceImpl)(schedulerTinkoff, schedulerDB))
}

case class TelegramContainerMess(mess: String, investInfoBot: InvestInfoBot)

class TelegramActorMess(monitoringServiceImpl: MonitoringServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl)(
    schedulerTinkoff: SchedulerService,
    schedulerDB: SchedulerService)
    extends Actor {
  val log: LoggingAdapter = Logging(context.system, this)
  def receive: Receive = {
    case mes: String => {
      log.info("NEW_MESSEND_FROM_TELEGRAM=" + mes)
      parsString(mes)
    }
    case mes: TelegramContainerMess => {
      log.info("NEW_MESSEND_FROM_TELEGRAM=" + mes.mess)
      parsString(mes)
    }
    case error => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsString(s: String): Unit = s match {
    case s if s.startsWith("/start") =>
      (for {
        p <- dataBaseServiceImpl.selectTinkoffToolsTbl(s.replace("/start ", ""))
        e = p.fold(localError, tinkoffToolsTbl)
      } yield ()).onErrorHandle(o => log.error(o.getMessage)).runAsyncAndForget(schedulerDB)
    case s if s.startsWith("/stop") => {
      monitoringServiceImpl.stopMonitoring(s.replace("/stop ", "")).runAsyncAndForget(schedulerTinkoff)
    }
    case s if s.startsWith("/help") => {}
    case s if s.startsWith("/log")  => {}
    case _                          => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }

  private def parsString(s: TelegramContainerMess): Unit = s match {
    case s if s.mess.startsWith("/start") =>
      (for {
        p <- dataBaseServiceImpl.selectTinkoffToolsTbl(s.mess.replace("/start ", ""))
        e = p.fold(localError, tinkoffToolsTbl)
        _ = log.info(e)
        _ = s.investInfoBot.sendMessage(e)
      } yield ()).onErrorHandle(o => log.error(o.getMessage)).runAsyncAndForget(schedulerDB)
    case s if s.mess.startsWith("/stop") => {
      monitoringServiceImpl.stopMonitoring(s.mess.replace("/stop ", "")).runAsyncAndForget(schedulerTinkoff)
    }
    case s if s.mess.startsWith("/help") => {}
    case s if s.mess.startsWith("/log")  => {}
    case _                               => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }

  private val localError: Throwable => String = i => "ERROR_MESSAG " + i.getMessage


  private val tinkoffToolsTbl: TinkoffToolsTbl => String = i => {
    monitoringServiceImpl.startMonitoring(i.figi)
    "Отправлен запрос на мониторинг " + i.name + " " + i.ticker + " " + i.figi + " "
  }

}

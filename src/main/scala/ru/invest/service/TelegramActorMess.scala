package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.config.PureFunction

import ru.invest.service.helpers.database.BDInvest
import ru.invest.core.logger.LoggerMessenger
import ru.mytelegrambot.InvestInfoBot

object TelegramActorMess {
  val START: String = "/start"
  val STOP: String  = "/stop"
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
  import TelegramActorMess._
  val log: LoggingAdapter = Logging(context.system, this)

  def receive: Receive = {
    case mes: TelegramContainerMess => {
      log.info("NEW_MESSAGE_FROM_TELEGTAM=" + mes.mess)
      parsStringd(mes)
    }
    case error => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsStringd(s: TelegramContainerMess): Unit = s match {
    case s if s.mess.startsWith(START) =>
      taskForFigi(s.copy(s.mess.replace(s"$START ", "")))(
        monitoringServiceImpl.startMonitoring,
        LoggerMessenger.TELEGRAM_RESPONSE_START).runAsyncAndForget(schedulerDB)
    case s if s.mess.startsWith(STOP) =>
      taskForFigi(s.copy(s.mess.replace(s"$STOP ", "")))(monitoringServiceImpl.startMonitoring,
                                                         LoggerMessenger.TELEGRAM_RESPONSE_STOP).runAsyncAndForget(schedulerDB)
    case _ => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }

  private def taskForFigi(s: TelegramContainerMess)(f: String => Task[_], l: (BDInvest, String) => String): Task[_] =
    (for {
      taskid <- PureFunction.getUUID
      p      <- dataBaseServiceImpl.selectTinkoffToolsTbl(s.mess)
      _      <- f(p.figi)
      mess   = l(p, taskid)
      _      = log.info(mess)
    } yield s.investInfoBot.sendMessage(mess))
      .onErrorHandle(o => {
        log.error(o.getMessage)
        s.investInfoBot.sendMessage(o.getMessage)
      })

}

package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.config.PureFunction

import ru.invest.service.helpers.database.{BDInvest, TinkoffToolsTbl}
import ru.invest.core.logger.LoggerMessenger
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
    case mes: TelegramContainerMess => parsStringd(mes)
    case error                      => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsStringd(s: TelegramContainerMess): Unit = s match {
    case s if s.mess.startsWith("/start") =>
      taskForFigi(s.copy(s.mess.replace("/start ", "")))(monitoringServiceImpl.startMonitoring,
                                                         LoggerMessenger.TELEGRAM_RECUEST_OK).runAsyncAndForget(schedulerDB)
    case s if s.mess.startsWith("/stop") =>
    // taskForFigi(s.copy(s.mess.replace("/stop ", "")))(monitoringServiceImpl.stopMonitoring,).runAsyncAndForget(schedulerDB)
    case _ => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }

  private def taskForFigi(s: TelegramContainerMess)(f: String => Task[_], l: (BDInvest, String) => String): Task[_] =
    (for {
      taskid <- PureFunction.getUUID
      _      = log.info("NEW_MESSAGE_FROM_TELEGTAM=" + s.mess)
      p      <- dataBaseServiceImpl.selectTinkoffToolsTbl(s.mess.replace("/start ", ""))
      _      <- f(p.figi)
      mess   = l(p, taskid)
      _      = log.info(mess)
    } yield s.investInfoBot.sendMessage(mess))
      .onErrorHandle(o => {
        log.error(o.getMessage)
        s.investInfoBot.sendMessage(o.getMessage)
      })

}

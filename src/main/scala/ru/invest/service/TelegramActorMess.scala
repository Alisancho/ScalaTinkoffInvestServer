package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{KillSwitches, SharedKillSwitch}
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.functions.PureFunction
import ru.invest.core.logger.LoggerMessenger
import ru.invest.entity.database.BDInvest
import ru.mytelegrambot.InvestInfoBot
import ru.invest.core.config.ConfigObject._
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
  import TelegramActorMess._
  val log: LoggingAdapter = Logging(context.system, this)

  var analysisFlag                                           = false
  var sharedKillSwitch: SharedKillSwitch                     = KillSwitches.shared("my-kill-switch")
  var businessProcessServiceImpl: BusinessProcessServiceImpl = _

  def receive: Receive = {
    case mes: TelegramContainerMess => {
      log.info("NEW_MESSAGE_FROM_TELEGTAM=" + mes.mess)
      parsStringd(mes)
    }
    case l: BusinessProcessServiceImpl => businessProcessServiceImpl = l
    case error                         => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsStringd(s: TelegramContainerMess): Unit = s match {
    case s if s.mess.startsWith(START) =>
      taskForFigi(s.copy(s.mess.replace(s"$START ", "")))(
        monitoringServiceImpl.startMonitoring,
        LoggerMessenger.TELEGRAM_RESPONSE_START).runAsyncAndForget(schedulerDB)
    case s if s.mess.startsWith(STOP) =>
      taskForFigi(s.copy(s.mess.replace(s"$STOP ", "")))(monitoringServiceImpl.startMonitoring,
                                                         LoggerMessenger.TELEGRAM_RESPONSE_STOP).runAsyncAndForget(schedulerDB)
    case s if s.mess.startsWith(ANALYTICS_START) =>
      if (analysisFlag) {
        s.investInfoBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag = true
        sharedKillSwitch = KillSwitches.shared("my-kill-switch")
        businessProcessServiceImpl.startAnalyticsJob(sharedKillSwitch).runAsyncAndForget(schedulerDB)
      }
    case s if s.mess.startsWith(ANALYTICS_STOP) =>
      if (analysisFlag) {
        sharedKillSwitch.shutdown()
        analysisFlag = false
      } else {
        s.investInfoBot.sendMessage("Сбор аналитики не запущен")
      }

    case s if s.mess.startsWith(UPDATE_TOOLS) => {
      businessProcessServiceImpl.ubdateTinkoffToolsTable.runAsyncAndForget(schedulerDB)
      s.investInfoBot.sendMessage("Запущено обновление таблицы")
    }


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

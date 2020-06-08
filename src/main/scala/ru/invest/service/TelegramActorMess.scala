package ru.invest.service

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{KillSwitches, SharedKillSwitch}
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.mytelegrambot.InvestInfoBot
import ru.invest.core.config.ConfigObject._
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.Instrument
object TelegramActorMess {

  def apply(schedulerTinkoff: SchedulerService): Props = Props(new TelegramActorMess(schedulerTinkoff))
}

case class TelegramContainerMess(mess: String, investInfoBot: InvestInfoBot)

class TelegramActorMess(schedulerTinkoff: SchedulerService) extends Actor {

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
    case s if s.mess.startsWith(ANALYTICS_START) =>
      if (analysisFlag) {
        s.investInfoBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag = true
        sharedKillSwitch = KillSwitches.shared("my-kill-switch")
        businessProcessServiceImpl
          .startAnalyticsJob(sharedKillSwitch)(fun(_, s))
          .runAsyncAndForget(schedulerTinkoff)
        s.investInfoBot.sendMessage("Успешный запуск сбора аналитики")
      }
    case s if s.mess == ANALYTICS_STOP =>
      if (analysisFlag) {
        sharedKillSwitch.shutdown()
        analysisFlag = false
        s.investInfoBot.sendMessage("Сбор аналитики остановлен")
      } else {
        s.investInfoBot.sendMessage("Сбор аналитики не запущен")
      }

    case _ => log.info("NEW_MESSEND_FROM_TELEGRAM=" + s)
  }
  val fun: (String, TelegramContainerMess) => Task[_] = (w, t) =>
    Task {
      t.investInfoBot.sendMessage(w)
  }
}

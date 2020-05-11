package ru.invest.service

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}

class TelegramActorMess extends Actor {
  val log: LoggingAdapter = Logging(context.system, this)
  def receive: Receive = {
    case a: String => {
      log.info("NEW_MESSEND_FROM_TELEGRAM=" + a)
      parsString(a)
    }
    case error     => log.error("ERROR_Receive=" + error.toString)
  }

  private def parsString(s: String): Unit = s match {
    case s if s.startsWith("/satrt") => {}
    case s if s.startsWith("/stop")  => {}
    case s if s.startsWith("/help")  => {}
    case s if s.startsWith("/log")   => {}
  }

}

package ru.invest.service

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.{ApiContext, TelegramBotsApi}
import ru.mytelegrambot.InvestInfoBot

class TelegramServiceImpl(token: String,
                          name: String,
                          chat_id: Long,
                          acctorRef: ActorRef,
                          host: Option[String],
                          port: Option[Int])
    extends LazyLogging {

  ApiContextInitializer.init()
  val telegramBotsApi = new TelegramBotsApi()

  val investBot: InvestInfoBot = (for {
    z <- host
    x <- port
  } yield new InvestInfoBot(token, name, getProxy(z, x), chat_id, acctorRef))
    .getOrElse(new InvestInfoBot(token, name, chat_id, acctorRef))

  telegramBotsApi.registerBot(investBot)

  private def getProxy(localHost: String, localPort: Int): DefaultBotOptions = {
    val bootOption: DefaultBotOptions = ApiContext.getInstance(classOf[DefaultBotOptions])
    bootOption.setProxyType(DefaultBotOptions.ProxyType.SOCKS5)
    bootOption.setProxyHost(localHost)
    bootOption.setProxyPort(localPort)
    bootOption
  }
}

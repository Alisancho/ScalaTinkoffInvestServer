package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.{ApiContext, TelegramBotsApi}
import ru.mytelegrambot.InvestInfoBot

class TelegramServiceImpl(host: String, port: Int, token: String, name: String) extends LazyLogging{

  ApiContextInitializer.init()
  private val bootOption: DefaultBotOptions = ApiContext.getInstance(classOf[DefaultBotOptions])
  bootOption.setProxyType(DefaultBotOptions.ProxyType.SOCKS5)
  bootOption.setProxyHost(host)
  bootOption.setProxyPort(port)

  private val investBot: InvestInfoBot = new InvestInfoBot(token, name, bootOption)
  private val telegramBotsApi  = new TelegramBotsApi()
  telegramBotsApi.registerBot(investBot)

}

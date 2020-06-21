package ru.invest.service

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import ru.mytelegrambot.InvestInfoBot

class TelegramServiceImpl(token: String, name: String, chat_id: Long, actorRef: ActorRef) extends LazyLogging {
  ApiContextInitializer.init()

  val telegramBotsApi = new TelegramBotsApi()

  val investBot: InvestInfoBot = new InvestInfoBot(token, name, chat_id, actorRef)

  telegramBotsApi.registerBot(investBot)

}

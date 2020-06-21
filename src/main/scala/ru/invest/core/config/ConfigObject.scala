package ru.invest.core.config

import com.typesafe.config.ConfigFactory
import com.softwaremill.tagging._
object ConfigObject {

  sealed trait Token
  private val conf                      = ConfigFactory.load()
  val TOKEN: String @@ Token            = conf.getString("tinkoff.mail").taggedWith[Token]
  val SERVER_HOST: String               = conf.getString("server.host")
  val SERVER_PORT: Int                  = conf.getInt("server.port")
  val TINKOFF_BROKER_ACCOUNT_ID: String = conf.getString("tinkoff.broker.account.id")

  val SCHEDULER_POOL_DB: Int            = conf.getInt("scheduler.pool.db")
  val SCHEDULER_POOL_TINKOFF: Int       = conf.getInt("scheduler.pool.tinkoff")

  val TELEGRAM_TOKEN: String   = conf.getString("telegtam.token")
  val TELEGRAM_NAMEBOT: String = conf.getString("telegtam.namebot")
  val TELEGRAM_CHAT_ID: Int    = conf.getInt("telegtam.chat.id")

  val START: String           = "/start"
  val STOP: String            = "/stop"
  val TASK_LIST: String       = "/tasks"
  val ANALYTICS_START: String = "Сбор аналитики"
  val ANALYTICS_STOP: String  = "Остановка сбора аналитики"
  val UPDATE_TOOLS:String = "Обновить базу с активами"
}

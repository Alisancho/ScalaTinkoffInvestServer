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
  val POOL_OPENAPI: Int                 = conf.getInt("pool.openapi")

  val TELEGRAM_TOKEN: String   = conf.getString("telegtam.token")
  val TELEGRAM_NAMEBOT: String = conf.getString("telegtam.namebot")
  val TELEGRAM_HOST: String    = conf.getString("telegtam.host")
  val TELEGRAM_PROXY: Boolean  = conf.getBoolean("telegtam.proxy")
  val TELEGRAM_PORT: Int       = conf.getInt("telegtam.port")
  val TELEGRAM_CHAT_ID: Int    = conf.getInt("telegtam.chat.id")
}

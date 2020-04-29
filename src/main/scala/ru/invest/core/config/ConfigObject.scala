package ru.invest.core.config

import com.typesafe.config.ConfigFactory
import com.softwaremill.tagging._
object ConfigObject {
  sealed trait Token
  private val conf                      = ConfigFactory.load()
  val TOKEN: String @@Token             = conf.getString("tinkoff.mail").taggedWith[Token]
  val SERVER_HOST: String               = conf.getString("server.host")
  val SERVER_PORT: Int                  = conf.getInt("server.port")
  val TINKOFF_BROKER_ACCOUNT_ID: String = conf.getString("tinkoff.broker.account.id")
}

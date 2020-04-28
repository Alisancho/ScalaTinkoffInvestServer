package ru.invest.core.config

import com.typesafe.config.ConfigFactory

object ConfigObject {
  private val conf = ConfigFactory.load()
  val TOKEN: String = conf.getString("tinkoff.mail")
}

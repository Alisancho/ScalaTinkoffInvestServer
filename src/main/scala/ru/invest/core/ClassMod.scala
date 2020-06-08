package ru.invest.core

import ru.tinkoff.invest.openapi.models.market.Instrument

object ClassMod {
  implicit class InstrumentMod(instrument: Instrument) {
    def toStringTelegramUp: String =
      s"""
         |Возможен рост актива:
         |FIGI = ${instrument.figi}
         |NAME = ${instrument.name}
         |TICKER = ${instrument.ticker}
         |URL = https://static.tinkoff.ru/brands/traiding/${instrument.isin}x160.png
         |""".stripMargin
    def toStringTelegramDown: String =
      s"""
         |Возможно падение актива:
         |FIGI = ${instrument.figi}
         |NAME = ${instrument.name}
         |TICKER = ${instrument.ticker}
         |""".stripMargin
    }
}

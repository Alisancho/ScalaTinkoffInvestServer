package ru.invest.core.logger

import ru.invest.service.helpers.database.BDInvest
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

object LoggerMessenger {
  val TELEGRAM_MESS: (BDInvest, StreamingEvent.Candle) => String = (bDInvest, candle) => s"""
       |Актив достиг цены ${candle.getClosingPrice} ${bDInvest.currency}
       |FIGI = ${bDInvest.figi}
       |NAME = ${bDInvest.name}
       |TICKER = ${bDInvest.ticker}
       |
       |""".stripMargin
  val TELEGRAM_RECUEST_OK: (BDInvest, String) => String          = (mess, taskID) => s"""
       |Отправлен запрос на мониторинг:
       |FIGI = ${mess.figi}
       |NAME = ${mess.name}
       |TICKER = ${mess.ticker}
       |TASK_ID = $taskID
       |""".stripMargin

  val TELEGRAM_RECUEST_ERROR: BDInvest => String = error => "Ошибка " + error
}

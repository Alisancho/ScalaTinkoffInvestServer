package ru.invest.core.logger

import ru.invest.entity.database.BDInvest
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

object LoggerMessenger {
  val TELEGRAM_MESS: (BDInvest, StreamingEvent.Candle) => String = (bDInvest, candle) => s"""
       |Актив достиг цены ${candle.getClosingPrice} ${bDInvest.currency}
       |FIGI = ${bDInvest.figi}
       |NAME = ${bDInvest.name}
       |TICKER = ${bDInvest.ticker}
       |""".stripMargin

  val TELEGRAM_RESPONSE_START: (BDInvest, String) => String = (mess, taskID) => s"""
       |Отправлен запрос на мониторинг:
       |FIGI = ${mess.figi}
       |NAME = ${mess.name}
       |TICKER = ${mess.ticker}
       |TASK_ID = $taskID
       |""".stripMargin

  val TELEGRAM_RESPONSE_STOP: (BDInvest, String) => String = (mess, taskID) => s"""
       |Отправлен запрос на остановку мониторинга:
       |FIGI = ${mess.figi}
       |NAME = ${mess.name}
       |TICKER = ${mess.ticker}
       |TASK_ID = $taskID
       |""".stripMargin

  val TELEGRAM_RESPONSE_ADD: (BDInvest, String) => String = (mess, taskID) => s"""
      |Создан новый task для мониторинга:
      |FIGI = ${mess.figi}
      |NAME = ${mess.name}
      |TICKER = ${mess.ticker}
      |TASK_ID = $taskID
      |""".stripMargin
}

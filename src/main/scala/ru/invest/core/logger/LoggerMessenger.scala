package ru.invest.core.logger

object LoggerMessenger {
  val TELEGRAM_MESS: (String, String) => String = (q, w) => "Актив " + q + " достиг цены " + w
}

package ru.invest.service.helpers.subscriber

import com.typesafe.scalalogging.LazyLogging
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

trait StreamingApiSubscriberLogger extends LogicForStreamingEvent with LazyLogging {
  override val funCandle: StreamingEvent.Candle => Unit                 = k => logger.info(k.toString)
  override val funOrderbook: StreamingEvent.Orderbook => Unit           = k => logger.info(k.toString)
  override val funInstrumentInfo: StreamingEvent.InstrumentInfo => Unit = k => logger.info(k.toString)
  override val funError: StreamingEvent.Error => Unit                   = k => logger.info(k.getError)
}

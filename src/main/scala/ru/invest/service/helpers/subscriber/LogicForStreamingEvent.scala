package ru.invest.service.helpers.subscriber

import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

trait LogicForStreamingEvent {
  val funCandle: StreamingEvent.Candle => Unit
  val funOrderbook: StreamingEvent.Orderbook => Unit
  val funInstrumentInfo: StreamingEvent.InstrumentInfo => Unit
  val funError: StreamingEvent.Error => Unit
}

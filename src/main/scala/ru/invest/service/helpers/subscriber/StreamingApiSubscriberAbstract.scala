package ru.invest.service.helpers.subscriber

import java.util.concurrent.Executor

import org.reactivestreams.example.unicast.AsyncSubscriber
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

abstract class StreamingApiSubscriberAbstract(val executor: Executor)
    extends AsyncSubscriber[StreamingEvent](executor) with LogicForStreamingEvent{
  override protected def whenNext(event: StreamingEvent): Boolean = {
    val ddd = event match {
      case candle: StreamingEvent.Candle       => funCandle(candle)
      case orderbook: StreamingEvent.Orderbook => funOrderbook(orderbook)
      case info: StreamingEvent.InstrumentInfo => funInstrumentInfo(info)
      case error: StreamingEvent.Error         => funError(error)
    }
    true
  }
}

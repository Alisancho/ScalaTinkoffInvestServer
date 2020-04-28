package ru.invest.service.helpers.monitoring

import java.util.concurrent.Executor
import java.util.logging.Logger

import org.reactivestreams.example.unicast.AsyncSubscriber

import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

class StreamingApiSubscriber(val logger: Logger, val executor: Executor)
    extends AsyncSubscriber[StreamingEvent](executor) {
  override protected def whenNext(event: StreamingEvent): Boolean = {
    event match {
      case candle: StreamingEvent.Candle       => "Candle"
      case orderbook: StreamingEvent.Orderbook => "Orderbook"
      case info: StreamingEvent.InstrumentInfo => "InstrumentInfo"
      case error: StreamingEvent.Error         => "Error"
    }
    logger.info("Пришло новое событие из Streaming API\n")
    true
  }
}

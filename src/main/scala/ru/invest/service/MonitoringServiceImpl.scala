package ru.invest.service
import java.util.concurrent.{Executor, Executors}
import java.util.logging.Logger

import ru.invest.service.helpers.monitoring.StreamingApiSubscriber
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory

class MonitoringServiceImpl(token: String, logger: Logger) {
  private val factory = new OkHttpOpenApiFactory(token, logger)
  private val api = factory.createOpenApiClient(Executors.newFixedThreadPool(4))
  private val listener = new StreamingApiSubscriber(logger, Executors.newSingleThreadExecutor)
  api.getStreamingContext.getEventPublisher.subscribe(listener)

  def startNewMonitoring(figi: String) = {
    api.getStreamingContext.sendRequest(
      StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN)
    )
  }
}
package ru.invest.service
import java.util.concurrent.{CompletionStage, Executor, Executors}
import java.util.logging.Logger

import com.softwaremill.tagging.@@
import monix.eval.Task
import ru.invest.core.config.ConfigObject.Token
import ru.invest.service.helpers.subscriber.MainSubscriber
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio
import ru.tinkoff.invest.openapi.models.streaming.StreamingRequest
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory
import ru.invest.service.helpers.tinkoffrest.{TinkoffMarket, TinkoffOrders}
import ru.tinkoff.invest.openapi.OpenApi

class TinkoffRESTServiceImpl(token: String @@ Token, val accountId: String, logger: Logger)
    extends TinkoffMarket with TinkoffOrders {
  val factory: OkHttpOpenApiFactory = new OkHttpOpenApiFactory(token, logger)
  val api: OpenApi                  = factory.createOpenApiClient(Executors.newFixedThreadPool(4))
  val listener: MainSubscriber      = new MainSubscriber(Executors.newSingleThreadExecutor)
  api.getStreamingContext.getEventPublisher.subscribe(listener)

  def startNewMonitoring(figi: String): Task[Unit] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))
  }

  def getPortfolio: Task[Portfolio] = Task.fromFuture {
    toScala(api.getPortfolioContext.getPortfolio(accountId))
  }
}

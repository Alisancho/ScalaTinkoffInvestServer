package ru.invest.service
import java.util.concurrent.{Executor, Executors}
import java.util.logging.Logger

import com.softwaremill.tagging.@@
import monix.eval.Task
import ru.invest.core.config.ConfigObject.Token
import ru.invest.service.helpers.monitoring.StreamingApiSubscriber
import ru.tinkoff.invest.openapi.models.market.{CandleInterval, InstrumentsList}
import ru.tinkoff.invest.openapi.models.orders.{LimitOrder, PlacedOrder}
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory
import cats.implicits._
class TinkoffRESTServiceImpl(token: String @@ Token, accountId: String, logger: Logger) {
  private val factory  = new OkHttpOpenApiFactory(token, logger)
  private val api      = factory.createOpenApiClient(Executors.newFixedThreadPool(4))
  private val listener = new StreamingApiSubscriber(logger, Executors.newSingleThreadExecutor)
  api.getStreamingContext.getEventPublisher.subscribe(listener)

  def startNewMonitoring(figi: String): Task[Unit] =
    Task {
      api.getStreamingContext.sendRequest(
        StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN)
      )
    }

  def getMarketStocks: Task[InstrumentsList] = Task {
    api.getMarketContext.getMarketStocks.join()
  }

  def getPortfolio: Task[Portfolio] = Task {
    api.getPortfolioContext.getPortfolio(accountId).join()
  }

  def limitOrders(figi:String,limitOrder: LimitOrder): Task[PlacedOrder] = Task {
    api.getOrdersContext.placeLimitOrder(figi,limitOrder,accountId).join()
  }
}

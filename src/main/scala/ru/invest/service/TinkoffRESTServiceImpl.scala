package ru.invest.service
import java.util.concurrent.{CompletionStage, Executor, Executors}
import java.util.logging.Logger

import com.softwaremill.tagging.@@
import monix.eval.Task
import ru.invest.core.config.ConfigObject.Token
import ru.invest.service.helpers.subscriber.MainSubscriber
import ru.tinkoff.invest.openapi.models.market.{CandleInterval, InstrumentsList}
import ru.tinkoff.invest.openapi.models.orders.{LimitOrder, PlacedOrder}
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory
import reactor.core.publisher.Flux

import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}
class TinkoffRESTServiceImpl(token: String @@ Token, accountId: String, logger:Logger) {
  private val factory  = new OkHttpOpenApiFactory(token, logger)
  private val api      = factory.createOpenApiClient(Executors.newFixedThreadPool(4))
  private val listener = new MainSubscriber(Executors.newSingleThreadExecutor)
  api.getStreamingContext.getEventPublisher.subscribe(listener)

  def getMarketStocks: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketStocks)
  }

  def getMarketEtfs: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketEtfs)
  }

  def getMarketBonds: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketBonds)
  }

  def getMarketCurrencies: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketCurrencies)
  }

  def startNewMonitoring(figi: String): Task[Unit] = Task {
      api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))
    }

  def getPortfolio: Task[Portfolio] = Task.fromFuture {
    toScala(api.getPortfolioContext.getPortfolio(accountId))
  }

  def limitOrders(figi: String, limitOrder: LimitOrder): Task[PlacedOrder] = Task.fromFuture {
    toScala(api.getOrdersContext.placeLimitOrder(figi, limitOrder, accountId))
  }

  def gool() = Flux.from[StreamingEvent](api.getStreamingContext.getEventPublisher)

  private def toScala[T](cs: CompletionStage[T]): Future[T] = {
    cs match {
      case cf: CF[T] => cf.wrapped
      case _ =>
        val p = new P[T](cs)
        cs whenComplete p
        p.future
    }
  }
}
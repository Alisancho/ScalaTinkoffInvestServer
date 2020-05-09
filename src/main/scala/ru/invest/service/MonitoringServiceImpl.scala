package ru.invest.service

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{KillSwitches, SharedKillSwitch}
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}

import scala.concurrent.Future

class MonitoringServiceImpl(api: OpenApi)(implicit system:ActorSystem) extends LazyLogging {
  val sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")
  val names: Future[Done] = Source
    .fromPublisher(api.getStreamingContext.getEventPublisher)
    .filter {
      case candle: StreamingEvent.Candle => {
        candle.getFigi == "BBG000BKNX95"
      }
      case orderbook: StreamingEvent.Orderbook => false
      case info: StreamingEvent.InstrumentInfo => false
      case error: StreamingEvent.Error         => false
    }
    .via(sharedKillSwitch.flow)
    .runForeach(i => println(i.toString))

  def startNewMonitoring(figi: String): Task[Unit] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))
  }
}

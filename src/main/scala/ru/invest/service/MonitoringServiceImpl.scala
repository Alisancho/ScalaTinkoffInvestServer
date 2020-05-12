package ru.invest.service

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, KillSwitches, SharedKillSwitch}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.invest.service.helpers.database.TaskMonitoringTbl
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}

class MonitoringServiceImpl(api: OpenApi)(implicit system: ActorSystem) extends LazyLogging {
  val sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")

  def startMonitoring(figi: String): Unit =
    api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))

  def stopMonitoring(figi: String): Task[Unit] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.unsubscribeCandle(figi, CandleInterval.FIVE_MIN))
  }

  val monitorGraph = RunnableGraph
    .fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val in = Source
        .fromPublisher(api.getStreamingContext.getEventPublisher)
      val out = Sink.ignore

      val f1 = Flow[StreamingEvent].map({
        case candle: StreamingEvent.Candle => {
          logger.info(candle.toString)
          candle
        }
        case q: StreamingEvent.Orderbook      => q
        case q: StreamingEvent.InstrumentInfo => q
        case q: StreamingEvent.Error          => q
      })

      in ~> f1 ~> out
      ClosedShape
    })
    .run()

}

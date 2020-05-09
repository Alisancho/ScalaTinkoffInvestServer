package ru.invest.service

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, KillSwitches, SharedKillSwitch}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}

import scala.concurrent.Future

class MonitoringServiceImpl(api: OpenApi)(implicit system: ActorSystem) extends LazyLogging {
  val sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")

//  val names: Future[Done] = Source
//    .fromPublisher(api.getStreamingContext.getEventPublisher)
//    .filter {
//      case candle: StreamingEvent.Candle => {
//        candle.getFigi == "BBG000BKNX95"
//      }
//      case orderbook: StreamingEvent.Orderbook => false
//      case info: StreamingEvent.InstrumentInfo => false
//      case error: StreamingEvent.Error         => false
//    }
//    .via(sharedKillSwitch.flow)
//    .runForeach(i => println(i.toString))

  def startNewMonitoring(figi: String): Task[Unit] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))
  }

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val in = Source
      .fromPublisher(api.getStreamingContext.getEventPublisher)
    val out = Sink.ignore

    val bcast = builder.add(Broadcast[StreamingEvent](2))
    val merge = builder.add(Merge[StreamingEvent](2))

    val f1, f2, f3, f4 = Flow[StreamingEvent].map({
      case candle: StreamingEvent.Candle => {
        println("+++++++" + candle.toString)
        candle
      }
      case q: StreamingEvent.Orderbook      => q
      case q: StreamingEvent.InstrumentInfo => q
      case q: StreamingEvent.Error          => q
    })

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
    bcast ~> f4 ~> merge
    ClosedShape
  })

}

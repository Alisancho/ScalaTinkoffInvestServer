package ru.invest.service
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{KillSwitches, SharedKillSwitch}
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.catnap.MVar
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.market.CandleInterval
import ru.tinkoff.invest.openapi.models.streaming.{StreamingEvent, StreamingRequest}
import ru.invest.core.logger.LoggerMessenger._
import ru.invest.entity.database.TaskMonitoringTbl

import scala.concurrent.Future

class MonitoringServiceImpl(api: OpenApi)(schedulerDB: SchedulerService)(implicit system: ActorSystem,
                                                                         schedulerTinkoff: SchedulerService)
    extends LazyLogging {
  val sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")

  def startMonitoring(figi: String): Task[_] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.FIVE_MIN))
  }

  def stopMonitoring(figi: String): Task[_] = Task {
    api.getStreamingContext.sendRequest(StreamingRequest.unsubscribeCandle(figi, CandleInterval.FIVE_MIN))
  }

  def mainStream(mVar: Task[MVar[Task, List[TaskMonitoringTbl]]], telSer: TelegramServiceImpl): RunnableGraph[NotUsed] =
    Source
      .fromPublisher(api.getStreamingContext.getEventPublisher)
      .filter({
        case candle: StreamingEvent.Candle => {
          logger.info(candle.toString)
          true
        }
        case orderbook: StreamingEvent.Orderbook => {
          logger.warn(orderbook.toString)
          false
        }
        case instrumentInfo: StreamingEvent.InstrumentInfo => {
          logger.warn(instrumentInfo.toString)
          false
        }
        case error: StreamingEvent.Error => {
          logger.error(error.getError)
          false
        }
      })
      .mapAsync(parallelism = 1)({
        case candle: StreamingEvent.Candle => futureTask(mVar, telSer, candle)
      })
      .to(Sink.ignore)

  def f1(mVar: Task[MVar[Task, List[TaskMonitoringTbl]]], telSer: TelegramServiceImpl) =
    Flow[StreamingEvent.Candle].mapAsync(parallelism = 1)({
      case candle: StreamingEvent.Candle => futureTask(mVar, telSer, candle)
    })

  def futureTask(mvar: Task[MVar[Task, List[TaskMonitoringTbl]]],
                 telServ: TelegramServiceImpl,
                 candle: StreamingEvent.Candle): Future[_] =
    (for {
      q <- mvar.runToFuture(schedulerTinkoff)
      o <- q.read.runToFuture(schedulerTinkoff)
      _ = logger.info(candle.toString)
      _ = if (o.map(l => l.figi).contains(candle.getFigi) && converter(o.filter(p => p.figi == candle.getFigi).head, candle)) {
        telServ.investBot.sendMessage(
          TELEGRAM_MESS(o.filter(z => z.figi == candle.getFigi).head, candle))
        stopMonitoring(candle.getFigi).runSyncStep(schedulerDB)
        stopMonitoring(candle.getFigi).runSyncStep(schedulerDB)
        stopMonitoring(candle.getFigi).runSyncStep(schedulerDB)
        stopMonitoring(candle.getFigi).runSyncStep(schedulerDB)
        stopMonitoring(candle.getFigi).runSyncStep(schedulerDB)
      }
    } yield "Unit").recover {
      case e: Throwable => logger.error(e.getMessage)
    }

  def converter(taskMonitoringTbl: TaskMonitoringTbl, candle: StreamingEvent.Candle): Boolean = {
    val percent: (BigDecimal, BigDecimal) => Double = (start, thisis) =>
      (((thisis * 100) / start) - 100).setScale(2, BigDecimal.RoundingMode.HALF_UP).doubleValue

    if (taskMonitoringTbl.taskOperation == "Sell" && taskMonitoringTbl.taskType == "PROCENT") {
      if (taskMonitoringTbl.percent < percent(taskMonitoringTbl.purchasePrice, candle.getClosingPrice))
        true
      else
        false
    } else if (taskMonitoringTbl.taskOperation == "Sell" && taskMonitoringTbl.taskType == "PRICE") {
      if (taskMonitoringTbl.salePrice < candle.getClosingPrice)
        true
      else
        false
    } else if (taskMonitoringTbl.taskOperation == "Bay" && taskMonitoringTbl.taskType == "PROCENT") {
      if (taskMonitoringTbl.percent > percent(taskMonitoringTbl.salePrice, candle.getClosingPrice))
        true
      else
        false
    } else if (taskMonitoringTbl.taskOperation == "Bay" && taskMonitoringTbl.taskType == "PRICE") {
      if (taskMonitoringTbl.purchasePrice < candle.getClosingPrice)
        true
      else
        false
    } else {
      false
    }
  }
}

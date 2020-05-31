package ru.invest.core.analytics.models
import java.time.OffsetDateTime

import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}
import AnalyticsTbl._
import com.typesafe.scalalogging.LazyLogging
object Absorption extends LazyLogging {

  implicit class ConverterAbsorption(k: HistoricalCandles) {
    def toAbsorption(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] = anal(k)(f)(schedulerDB)
  }

  def anal(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k   <- Task { l.candles.asScala.toList }
      q2 = k(k.size - 2)
      q1 = k.last
      _   = if (trendUp(q1, q2)) f(l.toAnalyticsTbl("ABSORPTION", "UP")).runAsyncAndForget(schedulerDB)
      _   = if (trendDown(q1, q2)) f(l.toAnalyticsTbl("ABSORPTION", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()

  private val trendUp: (Candle, Candle) => Boolean = (q1, q2) => {
    if (q2.highestPrice.doubleValue() < q1.closePrice
          .doubleValue() && q2.lowestPrice.doubleValue > q1.openPrice.doubleValue) {
      true
    } else {
      false
    }
  }

  private val trendDown: (Candle, Candle) => Boolean = (q1, q2) => {
    if (q2.highestPrice.doubleValue() < q1.openPrice
          .doubleValue() && q2.lowestPrice.doubleValue > q1.closePrice.doubleValue) {
      true
    } else {
      false
    }
  }
}

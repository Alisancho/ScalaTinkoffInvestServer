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
      one = k.filter(r => r.time.getDayOfMonth == OffsetDateTime.now().minusDays(2).getDayOfMonth).head
      two = k.filter(r => r.time.getDayOfMonth == OffsetDateTime.now().minusDays(1).getDayOfMonth).head
      _   = if (trendUp(one, two)) f(l.toAnalyticsTbl("ABSORPTION", "UP")).runAsyncAndForget(schedulerDB)
      _   = if (trendDown(one, two)) f(l.toAnalyticsTbl("ABSORPTION", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()

  private val trendUp: (Candle, Candle) => Boolean = (one, two) => {
    if (one.highestPrice.doubleValue() < two.closePrice
          .doubleValue() && one.lowestPrice.doubleValue > two.openPrice.doubleValue) {
      true
    } else {
      false
    }
  }

  private val trendDown: (Candle, Candle) => Boolean = (one, two) => {
    if (one.highestPrice.doubleValue() < two.openPrice
          .doubleValue() && one.lowestPrice.doubleValue > two.closePrice.doubleValue) {
      true
    } else {
      false
    }
  }
}

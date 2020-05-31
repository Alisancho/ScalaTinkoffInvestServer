package ru.invest.core.analytics.models

import java.time.OffsetDateTime

import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}
import AnalyticsTbl._
object Hammer {

  implicit class ConverterHammer(k: HistoricalCandles) {
    def toHammer(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] = anal(k)(f)(schedulerDB)
  }

  def anal(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k <- Task {
            l.candles.asScala.toList
          }
      q4 = k(k.size - 4)
      q3 = k(k.size - 3)
      q2 = k(k.size - 2)
      q1 = k.last
      _  = if (trendUp(q1, q2, q3, q4) && q2.isHammerUp) f(l.toAnalyticsTbl("HUMMER", "UP")).runAsyncAndForget(schedulerDB)
//      _  = if (trendDown(q1, q2, q3, q4)) f(l.toAnalyticsTbl("HUMMER", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()

  private val trendUp: (Candle, Candle, Candle, Candle) => Boolean = (q1, q2, q3, q4) => {
    if (p1(q4) > p1(q3) && p1(q3) > p1(q2) && p1(q2) < p1(q1)) {
      true
    } else {
      false
    }
  }

//  private val trendDown: (Candle, Candle, Candle, Candle) => Boolean = (q1, q2, q3, q4) => {
//    if (one.highestPrice.doubleValue() < two.openPrice
//          .doubleValue() && one.lowestPrice.doubleValue > two.closePrice.doubleValue) {
//      true
//    } else {
//      false
//    }
//  }

  implicit class IsHammer(candle: Candle) {
    def isHammerUp: Boolean = {
      if (candle.closePrice.doubleValue() > p1(candle) && candle.openPrice.doubleValue() > p1(candle)) {
        if ((candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs < (candle.lowestPrice
              .doubleValue() - candle.highestPrice.doubleValue()).abs) {
          return true
        }
      }
      false
    }
  }

  private val p1: Candle => Double = q1 =>
    q1.closePrice.doubleValue().max(q1.openPrice.doubleValue() - q1.closePrice.doubleValue().min(q1.openPrice.doubleValue()))

}

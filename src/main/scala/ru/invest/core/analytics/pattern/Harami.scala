package ru.invest.core.analytics.pattern
import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}
import AnalyticsTbl._
import ru.invest.core.analytics.СandleMod._
trait Harami {
  def harami(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k <- Task {
        l.candles.asScala.toList
      }
      q5 = k(k.size - 5)
      q4 = k(k.size - 4)
      q3 = k(k.size - 3)
      q2 = k(k.size - 2)
      q1 = k.last
      _  = if ((q5, q4, q3).trendDown && (q2,q1).trendUp && harami2Up(q2,q3))
        f(l.toAnalyticsTbl("HARAMI", "UP")).runAsyncAndForget(schedulerDB)
      _  = if ((q5, q4, q3).trendUp && (q2,q1).trendDown && harami2Down(q2,q3))
        f(l.toAnalyticsTbl("HARAMI", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()

  private val harami2Up:(Candle, Candle) => Boolean = (q1,q2) =>
      q1.isGreen && q2.isRed &&
      q2.openPrice.doubleValue() > q1.closePrice.doubleValue() &&
      q2.openPrice.doubleValue() > q1.openPrice.doubleValue() &&
      q2.closePrice.doubleValue() < q1.openPrice.doubleValue() &&
      q2.closePrice.doubleValue() < q1.closePrice.doubleValue()

  private val harami2Down:(Candle, Candle) => Boolean = (q1,q2) =>
    q1.isRed && q2.isGreen &&
      q2.openPrice.doubleValue() < q1.closePrice.doubleValue() &&
      q2.openPrice.doubleValue() < q1.openPrice.doubleValue() &&
      q2.closePrice.doubleValue() > q1.openPrice.doubleValue() &&
      q2.closePrice.doubleValue() > q1.closePrice.doubleValue()

}

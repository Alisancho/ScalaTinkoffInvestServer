package ru.invest.core.analytics.pattern
import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}

trait Harami {
  def harami(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k <- Task {
        l.candles.asScala.toList
      }
      q4 = k(k.size - 4)
      q3 = k(k.size - 3)
      q2 = k(k.size - 2)
      q1 = k.last
      _  = if (trendUp(q1, q2, q3, q4) && q2.isHammer) f(l.toAnalyticsTbl("HUMMER", "UP")).runAsyncAndForget(schedulerDB)
      _  = if (trendDown(q1, q2, q3, q4) && q2.isHammer) f(l.toAnalyticsTbl("HUMMER", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()

  private val trendUp: (Candle, Candle, Candle, Candle) => Boolean = (q1, q2, q3, q4) =>
    q4.middleWidth > q3.middleWidth && q3.middleWidth > q2.middleWidth && q2.middleWidth < q1.middleWidth

  private val trendDown: (Candle, Candle, Candle, Candle) => Boolean = (q1, q2, q3, q4) =>
    q4.middleWidth < q3.middleWidth && q3.middleWidth < q2.middleWidth && q2.middleWidth > q1.middleWidth

}

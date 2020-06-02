package ru.invest.core.analytics.pattern
import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}
import AnalyticsTbl._
import ru.invest.core.analytics.СandleMod._

trait Hammer {
  def hammer(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k <- Task {l.candles.asScala.toList}
      q4 = k(k.size - 4)
      q3 = k(k.size - 3)
      q2 = k(k.size - 2)
      q1 = k.last
      _  = if ((q2, q1).trendUp && (q4, q3, q2).trendDown && q2.isHammer)
        f(l.toAnalyticsTbl("HUMMER", "UP")).runAsyncAndForget(schedulerDB)
      _  = if ((q2, q1).trendDown && (q4, q3, q2).trendUp && q2.isHammer)
        f(l.toAnalyticsTbl("HUMMER", "DOWN")).runAsyncAndForget(schedulerDB)
    } yield ()
}

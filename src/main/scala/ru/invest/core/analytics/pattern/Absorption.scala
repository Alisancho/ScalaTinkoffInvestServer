package ru.invest.core.analytics.pattern
import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles, Instrument}
import AnalyticsTbl._
import ru.invest.core.analytics.СandleMod._
import ru.invest.core.ClassMod._
trait Absorption {
  def absorption(l: HistoricalCandles)(instrument:Instrument)(f: String => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      k  <- Task { l.candles.asScala.toList }
      q2 = k(k.size - 2)
      q1 = k.last
      _  = if (q1.isAbsorptionUp(q2))
        f(instrument.toStringTelegramUp).runAsyncAndForget(schedulerDB)
      _  = if (q1.isAbsorptionDown(q2))
        f(instrument.toStringTelegramDown).runAsyncAndForget(schedulerDB)
    } yield ()
}

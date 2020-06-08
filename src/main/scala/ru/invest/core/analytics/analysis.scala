package ru.invest.core.analytics

import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.pattern.{Absorption, Hammer, Harami}
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.{HistoricalCandles, Instrument}

object analysis extends Hammer with Absorption with Harami{

  implicit class ConverterAbsorption(k: HistoricalCandles) {
    def toAbsorption(f: String => Task[_])(instrument:Instrument)(schedulerDB: SchedulerService): Task[_] = absorption(k)(instrument)(f)(schedulerDB)
  }

  implicit class ConverterHammer(k: HistoricalCandles) {
    def toHammer(f: String => Task[_])(instrument:Instrument)(schedulerDB: SchedulerService): Task[_] = hammer(k)(instrument)(f)(schedulerDB)
  }

  implicit class ConverterHarami(k: HistoricalCandles) {
    def toHarami(f: String => Task[_])(instrument:Instrument)(schedulerDB: SchedulerService): Task[_] = harami(k)(instrument)(f)(schedulerDB)
  }
}

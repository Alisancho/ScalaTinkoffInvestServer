package ru.invest.core.analytics

import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.pattern.{Absorption, Hammer, Harami}
import ru.invest.entity.database.AnalyticsTbl
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles

object analysis extends Hammer with Absorption with Harami{

  implicit class ConverterAbsorption(k: HistoricalCandles) {
    def toAbsorption(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] = absorption(k)(f)(schedulerDB)
  }

  implicit class ConverterHammer(k: HistoricalCandles) {
    def toHammer(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] = hammer(k)(f)(schedulerDB)
  }

  implicit class ConverterHarami(k: HistoricalCandles) {
    def toHarami(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] = harami(k)(f)(schedulerDB)
  }
}

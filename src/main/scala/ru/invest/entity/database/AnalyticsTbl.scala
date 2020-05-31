package ru.invest.entity.database

import java.time.Instant

import ru.invest.core.functions.PureFunction
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles

case class AnalyticsTbl(idanalytics: String, typeAnalytics: String, figi: String, datatask: Instant, trend: String)

object AnalyticsTbl {
  implicit class Colp(l: HistoricalCandles) {
    def toAnalyticsTbl(analyticTupe: String, trend: String): AnalyticsTbl =
      AnalyticsTbl(PureFunction.getUUIDString, analyticTupe, l.figi, Instant.now(), trend)
  }


}

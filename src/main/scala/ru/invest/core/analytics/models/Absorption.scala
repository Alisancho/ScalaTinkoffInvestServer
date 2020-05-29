package ru.invest.core.analytics.models
import java.time.OffsetDateTime

import akka.util.ccompat.JavaConverters._
import monix.eval.Task
import ru.tinkoff.invest.openapi.models.market.{Candle, HistoricalCandles}

class Absorption {
  def hu(l: HistoricalCandles): Task[Boolean] =
    for {
      k   <- Task { l.candles.asScala.toList }
      one = k.filter(r => r.time == OffsetDateTime.now().minusDays(2)).head
      two = k.filter(r => r.time == OffsetDateTime.now().minusDays(1)).head
    } yield up(one, two)

 private val up: (Candle, Candle) => Boolean = (one, two) => {
    if (one.highestPrice.doubleValue() < two.closePrice
          .doubleValue() && one.lowestPrice.doubleValue > two.openPrice.doubleValue) {
      true
    } else {
      false
    }
  }
}

package ru.invest.core.analytics

import ru.tinkoff.invest.openapi.models.market.Candle

object СandleMod {

  implicit class СandleModType(candle: Candle) {
    def bodyWidth: Double = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs

    def shadowWidth: Double = (candle.lowestPrice.doubleValue() - candle.highestPrice.doubleValue()).abs

    def bodyMiddle: Double = {
      val step = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs / 2
      val min  = candle.closePrice.doubleValue().min(candle.openPrice.doubleValue())
      min + step
    }

    def shadowMiddle: Double = {
      val step = (candle.lowestPrice.doubleValue() - candle.highestPrice.doubleValue()).abs / 2
      val min  = candle.lowestPrice.doubleValue().min(candle.highestPrice.doubleValue())
      min + step
    }

    def isAbsorptionUp(q2: Candle): Boolean =
      q2.highestPrice.doubleValue() < candle.closePrice
        .doubleValue() && q2.lowestPrice.doubleValue > candle.openPrice.doubleValue

    def isAbsorptionDown(q2: Candle): Boolean =
      q2.highestPrice.doubleValue() < candle.openPrice
        .doubleValue() && q2.lowestPrice.doubleValue > candle.closePrice.doubleValue

    def isHammer: Boolean =
      candle.openPrice.doubleValue().min(candle.closePrice.doubleValue()) > shadowMiddle && bodyWidth * 3 < shadowWidth

    def isRed: Boolean   = candle.openPrice.doubleValue() > candle.closePrice.doubleValue()
    def isGreen: Boolean = candle.openPrice.doubleValue() < candle.closePrice.doubleValue()
  }

  implicit class ClassTrend3(tuple3: (Candle, Candle, Candle)) {
    def trendDown: Boolean =
      tuple3._1.bodyMiddle > tuple3._2.bodyMiddle &&
        tuple3._2.bodyMiddle > tuple3._3.bodyMiddle

    def trendUp: Boolean =
      tuple3._1.bodyMiddle < tuple3._2.bodyMiddle &&
        tuple3._2.bodyMiddle < tuple3._3.bodyMiddle
  }

  implicit class ClassTrend2(tuple3: (Candle, Candle)) {
    def trendDown: Boolean =
      tuple3._1.bodyMiddle > tuple3._2.bodyMiddle

    def trendUp: Boolean =
      tuple3._1.bodyMiddle < tuple3._2.bodyMiddle
  }
}

package ru.invest.core.analytics

import ru.tinkoff.invest.openapi.models.market.Candle

object СandleMod {

  implicit class СandleModType(candle: Candle) {
    def bodyWidth: Double = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs

    def bodyShadow: Double = (candle.lowestPrice.doubleValue() - candle.highestPrice.doubleValue()).abs

    def middleWidth: Double = {
      val step = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs / 2
      val min  = candle.closePrice.doubleValue().min(candle.openPrice.doubleValue())
      min + step
    }

    def middleShadow: Double = {
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

    def isHammer(): Boolean = {

    }

  }

}

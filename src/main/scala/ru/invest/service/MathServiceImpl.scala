package ru.invest.service

object MathServiceImpl {
  val percent: (BigDecimal, BigDecimal) => Double = (start, thisis) =>
    (((thisis * 100) / start) - 100).setScale(2, BigDecimal.RoundingMode.HALF_UP).doubleValue
}

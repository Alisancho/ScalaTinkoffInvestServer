package ru.invest.entity.database

import ru.tinkoff.invest.openapi.models.portfolio.Portfolio

case class TaskMonitoringTbl(figi: String,
                             name: String,
                             ticker: String,
                             currency: String,
                             purchasePrice: BigDecimal,
                             purchaseLot: Int,
                             salePrice: BigDecimal,
                             saleLot: Int,
                             percent: Double,
                             taskOperation: String,
                             taskType: String,
                             taskStatus: String) extends BDInvest

object TaskMonitoringTbl {
  implicit def convert(l: Portfolio.PortfolioPosition) =
    TaskMonitoringTbl(
      figi = l.figi,
      name = l.name,
      ticker = l.ticker,
      currency = l.expectedYield.currency.name(),
      purchasePrice = l.averagePositionPrice.value,
      purchaseLot = l.lots,
      salePrice = l.averagePositionPrice.value,
      saleLot = l.lots,
      percent = 10,
      taskOperation = "Sell",
      taskType = "PROCENT",
      taskStatus = "OFF"
    )
}

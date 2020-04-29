package ru.invest.service.helpers.database

import java.math.BigDecimal
import java.time.Instant

import ru.tinkoff.invest.openapi.models.portfolio.Portfolio.PortfolioPosition

case class TaskMonitoring(figi: String,
                          name: String,
                          currency: String,
                          status: String,
                          operation: String,
                          price_buy: Double,
                          lots_buy: Int,
                          price_sell: Double,
                          lots_sell: Int,
                          dataCreate: Instant,
                          dataUpdate: Instant)

object TaskMonitoring {
  implicit def convertToTaskMonitoring(portfolioPosition: PortfolioPosition) = TaskMonitoring(
    portfolioPosition.figi,
    portfolioPosition.name,
    portfolioPosition.expectedYield.currency.name(),
    "OFF",
    "Sell",
    portfolioPosition.expectedYield.value.doubleValue(),
    portfolioPosition.lots,
    portfolioPosition.expectedYield.value.doubleValue(),
    portfolioPosition.lots,
    Instant.now(),
    Instant.now()
  )
}

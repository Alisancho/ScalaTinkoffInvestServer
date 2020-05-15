package ru.invest.service.helpers.database

import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

case class TaskMonitoringTbl(taskId: String,
                             figi: String,
                             name: String,
                             currency: String,
                             purchasePrice: BigDecimal,
                             purchaseLot: Int,
                             salePrice: BigDecimal,
                             saleLot: Int,
                             percent: Double,
                             taskOperation: String,
                             taskType: String,
                             taskStatus: String)

object TaskMonitoringTbl {

}

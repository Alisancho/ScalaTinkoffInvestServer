package ru.invest.service.helpers.database

import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

case class TaskMonitoringTbl(taskId: String,
                             figi: String,
                             name: String,
                             purchasePrice: BigDecimal,
                             currency: String,
                             task_status: String,
                             taskOperation: String,
                             operation: String,
                             taskType: String,
                             percent: Double,
                             lot: Int)

object TaskMonitoringTbl {
  implicit def converter(taskMonitoringTbl:TaskMonitoringTbl,candle:StreamingEvent.Candle):Boolean = {
    if(taskMonitoringTbl.operation == "Bay")
  }
}

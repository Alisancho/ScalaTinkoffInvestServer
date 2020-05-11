package ru.invest.service.helpers.database

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

object TaskMonitoringTbl {}

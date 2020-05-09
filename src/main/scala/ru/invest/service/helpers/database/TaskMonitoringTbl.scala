package ru.invest.service.helpers.database

case class TaskMonitoringTbl(id: String,
                             figi: String,
                             name: String,
                             purchase_price: BigDecimal,
                             status: String,
                             operation: String,
                             percent: Double,
                             lot:Int)

object TaskMonitoringTbl {}

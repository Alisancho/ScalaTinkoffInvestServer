package ru.invest.service

import monix.eval.Task
import ru.invest.MyContext
import ru.invest.service.helpers.database.TaskMonitoring

class DataBaseServiceImpl(implicit val ctx: MyContext) {
  import ctx._
  def insertTaskMonitoring(taskMonitoring: TaskMonitoring): Task[Long] =
    ctx
      .run(
        query[TaskMonitoring].insert(
          _.figi       -> lift(taskMonitoring.figi),
          _.name       -> lift(taskMonitoring.name),
          _.currency   -> lift(taskMonitoring.currency),
          _.status     -> lift(taskMonitoring.status),
          _.operation  -> lift(taskMonitoring.operation),
          _.price_buy  -> lift(taskMonitoring.price_buy),
          _.lots_buy   -> lift(taskMonitoring.lots_buy),
          _.price_sell -> lift(taskMonitoring.price_sell),
          _.lots_sell  -> lift(taskMonitoring.lots_sell),
          _.dataCreate -> lift(taskMonitoring.dataCreate),
          _.dataUpdate -> lift(taskMonitoring.dataUpdate),
        )
      )
}

package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.invest.core.config.MyContext
import ru.invest.service.helpers.database.{TaskMonitoringTbl, TinkoffToolsTbl}

class DataBaseServiceImpl(implicit val ctx: MyContext) extends LazyLogging {
  import ctx._

  def selectTaskMonitoring: Task[List[TaskMonitoringTbl]] = ctx.run(query[TaskMonitoringTbl].filter(l => l.taskStatus == "ON"))

  def insertTinkoffTools(taskMonitoring: TinkoffToolsTbl): Task[Long] =
    ctx
      .run(
        query[TinkoffToolsTbl]
          .insert(
            _.figi             -> lift(taskMonitoring.figi),
            _.name             -> lift(taskMonitoring.name),
            _.currency         -> lift(taskMonitoring.currency),
            _.ticker           -> lift(taskMonitoring.ticker),
            _.lot              -> lift(taskMonitoring.lot),
            _.instruments_type -> lift(taskMonitoring.instruments_type),
            _.isin             -> lift(taskMonitoring.isin),
          )
          .onConflictUpdate(
            (t, e) => t.figi             -> e.figi,
            (t, e) => t.name             -> e.name,
            (t, e) => t.currency         -> e.currency,
            (t, e) => t.ticker           -> e.ticker,
            (t, e) => t.lot              -> e.lot,
            (t, e) => t.instruments_type -> e.instruments_type,
            (t, e) => t.isin             -> e.isin,
          )
      )

  def insertTaskMonitoringTbl(taskMonitoring: TaskMonitoringTbl): Task[Long] =
    ctx
      .run(
        query[TaskMonitoringTbl]
          .insert(
            _.figi          -> lift(taskMonitoring.figi),
            _.name          -> lift(taskMonitoring.name),
            _.ticker        -> lift(taskMonitoring.ticker),
            _.currency      -> lift(taskMonitoring.currency),
            _.purchasePrice -> lift(taskMonitoring.purchasePrice),
            _.purchaseLot   -> lift(taskMonitoring.purchaseLot),
            _.saleLot       -> lift(taskMonitoring.saleLot),
            _.salePrice     -> lift(taskMonitoring.salePrice),
            _.percent       -> lift(taskMonitoring.percent),
            _.taskStatus    -> lift(taskMonitoring.taskStatus),
            _.taskType      -> lift(taskMonitoring.taskType),
            _.taskOperation -> lift(taskMonitoring.taskOperation),
          )
          .onConflictUpdate(
            (t, e) => t.figi          -> e.figi,
            (t, e) => t.name          -> e.name,
            (t, e) => t.ticker        -> e.ticker,
            (t, e) => t.currency      -> e.currency,
            (t, e) => t.purchasePrice -> e.purchasePrice,
            (t, e) => t.purchaseLot   -> e.purchaseLot,
            (t, e) => t.saleLot       -> e.saleLot,
            (t, e) => t.salePrice     -> e.salePrice,
            (t, e) => t.percent       -> e.percent,
            (t, e) => t.taskStatus    -> e.taskStatus,
            (t, e) => t.taskType      -> e.taskType,
            (t, e) => t.taskOperation -> e.taskOperation,
          )
      )

  def selectTicker(ticker: String): Task[List[TinkoffToolsTbl]] =
    ctx.run(query[TinkoffToolsTbl].filter(l => l.figi == lift(ticker)))

}

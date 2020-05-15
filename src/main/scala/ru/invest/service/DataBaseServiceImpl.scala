package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.invest.core.config.MyContext
import ru.invest.service.helpers.database.{FigiMonitoringTbl, TaskMonitoringTbl, TinkoffToolsTbl}

class DataBaseServiceImpl(implicit val ctx: MyContext) extends LazyLogging {
  import ctx._

  def selectFIGIMonitoring: Task[List[FigiMonitoringTbl]] = ctx.run(query[FigiMonitoringTbl])
  def selectTaskMonitoring: Task[List[TaskMonitoringTbl]] = ctx.run(query[TaskMonitoringTbl].filter(l => l.taskStatus == "ON"))

  def insertFIGIMonitoring(taskMonitoring: FigiMonitoringTbl): Task[Long] =
    ctx
      .run(
        query[FigiMonitoringTbl]
          .insert(
            _.figi -> lift(taskMonitoring.figi),
            _.name -> lift(taskMonitoring.name),
          )
          .onConflictUpdate(
            (t, e) => t.figi -> e.figi,
            (t, e) => t.name -> e.name,
          )
      )

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

  def selectTicker(ticker: String): Task[List[TinkoffToolsTbl]] =
    ctx.run(query[TinkoffToolsTbl].filter(l => l.figi == lift(ticker)))

}

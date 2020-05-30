package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import ru.invest.core.context.MyContext
import ru.invest.entity.database.{AnalyticsTbl, TaskMonitoringTbl, TinkoffToolsTbl}

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
            (t, e) => t.percent       -> t.percent,
            (t, e) => t.taskStatus    -> t.taskStatus,
            (t, e) => t.taskType      -> t.taskType,
            (t, e) => t.taskOperation -> t.taskOperation,
          )
      )

  private val selectFIGIFromTicker: String => Task[List[TinkoffToolsTbl]] = ticker =>
    ctx.run(query[TinkoffToolsTbl].filter(l => l.ticker == lift(ticker)))

  private val selectFIGIFromName: String => Task[List[TinkoffToolsTbl]] = ticker =>
    ctx.run(query[TinkoffToolsTbl].filter(l => l.name == lift(ticker)))

  private val selectFIGIFromFigi: String => Task[List[TinkoffToolsTbl]] = ticker =>
    ctx.run(query[TinkoffToolsTbl].filter(l => l.figi == lift(ticker)))

  val selectTinkoffToolsTbl: String => Task[TinkoffToolsTbl] = name =>
    Task.parZip3(selectFIGIFromFigi(name), selectFIGIFromName(name), selectFIGIFromTicker(name)).map {
      case (fromFigi, fromName, tromTicker) => {
        if (fromFigi.nonEmpty) {
          fromFigi.head
        } else if (fromName.nonEmpty) {
          fromName.head
        } else if (tromTicker.nonEmpty) {
          tromTicker.head
        } else {
          throw new RuntimeException("В базе нет значения " + name)
        }
      }
  }
  def insertAnalyticsTblTbl(taskMonitoring: AnalyticsTbl): Task[Long] =
    ctx
      .run(
        query[AnalyticsTbl]
          .insert(
            _.idanalytics   -> lift(taskMonitoring.idanalytics),
            _.typeAnalytics -> lift(taskMonitoring.typeAnalytics),
            _.figi          -> lift(taskMonitoring.figi),
            _.datatask      -> lift(taskMonitoring.datatask),
            _.trend         -> lift(taskMonitoring.trend)
          )
          .onConflictUpdate(
            (t, e) => t.typeAnalytics -> e.typeAnalytics,
            (t, e) => t.figi          -> e.figi,
            (t, e) => t.datatask      -> e.datatask,
            (t, e) => t.trend         -> e.trend
          )
      )

}

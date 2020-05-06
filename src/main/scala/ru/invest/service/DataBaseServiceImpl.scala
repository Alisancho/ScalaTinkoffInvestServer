package ru.invest.service

import monix.eval.Task
import ru.invest.MyContext
import ru.invest.service.helpers.database.{FigiMonitoring, TinkoffTools}

class DataBaseServiceImpl(implicit val ctx: MyContext) {
  import ctx._

  def selectFIGIMonitoring: Task[List[FigiMonitoring]] = ctx.run(query[FigiMonitoring])

  def insertFIGIMonitoring(taskMonitoring: FigiMonitoring): Task[Long] =
    ctx
      .run(
        query[FigiMonitoring]
          .insert(
            _.figi -> lift(taskMonitoring.figi),
            _.name -> lift(taskMonitoring.name),
          )
          .onConflictUpdate(
            (t, e) => t.figi -> e.figi,
            (t, e) => t.name -> e.name,
          )
      )

  def insertTinkoffTools(taskMonitoring: TinkoffTools): Task[Long] =
    ctx
      .run(
        query[TinkoffTools]
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
}

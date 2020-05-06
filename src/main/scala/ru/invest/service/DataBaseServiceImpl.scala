package ru.invest.service

import monix.eval.Task
import ru.invest.MyContext
import ru.invest.service.helpers.database.{TaskMonitoring, TinkoffTools}
// def onConflictUpdate(assign: ((E, E) => (Any, Any)), assigns: ((E, E) => (Any, Any))*): Insert[E] = NonQuotedException()
class DataBaseServiceImpl(implicit val ctx: MyContext) {
  import ctx._
  def insertTaskMonitoring(taskMonitoring: TaskMonitoring): Task[Long] =
    ctx
      .run(
        query[TaskMonitoring]
          .insert(
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
          .onConflictUpdate(
            (t, e) => t.figi       -> e.figi,
            (t, e) => t.name       -> e.name,
            (t, e) => t.currency   -> e.currency,
            (t, e) => t.status     -> e.status,
            (t, e) => t.operation  -> e.operation,
            (t, e) => t.price_buy  -> e.price_buy,
            (t, e) => t.lots_buy   -> e.lots_buy,
            (t, e) => t.price_sell -> e.price_sell,
            (t, e) => t.lots_sell  -> e.lots_sell,
            (t, e) => t.dataCreate -> e.dataCreate,
            (t, e) => t.dataUpdate -> e.dataUpdate,
          )
      )
  def insertTinkoffTools(taskMonitoring: TinkoffTools): Task[Long] =
    ctx
      .run(
        query[TinkoffTools]
          .insert(
            _.figi                -> lift(taskMonitoring.figi),
            _.name                -> lift(taskMonitoring.name),
            _.currency            -> lift(taskMonitoring.currency),
            _.ticker              -> lift(taskMonitoring.ticker),
            _.lot                 -> lift(taskMonitoring.lot),
            _.instruments_type    -> lift(taskMonitoring.instruments_type),
            _.isin                -> lift(taskMonitoring.isin),
          )
          .onConflictUpdate(
            (t, e) => t.figi                -> e.figi,
            (t, e) => t.name                -> e.name,
            (t, e) => t.currency            -> e.currency,
            (t, e) => t.ticker              -> e.ticker,
            (t, e) => t.lot                 -> e.lot,
            (t, e) => t.instruments_type    -> e.instruments_type,
            (t, e) => t.isin                -> e.isin,
          )
      )
}

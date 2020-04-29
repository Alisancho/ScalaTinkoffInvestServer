package ru.invest.service

import monix.eval.Task
import ru.invest.MyContext
import ru.invest.service.helpers.database.MarketInstruments

class DataBaseServiceImpl(implicit ctx: MyContext) {
  import ctx._
  def setBlackListFriend(marketInstruments: MarketInstruments): Task[Long] =
    ctx
      .run(
        query[MarketInstruments].insert(
          _.figi              -> lift(marketInstruments.figi),
          _.name              -> lift(marketInstruments.name),
          _.currency          -> lift(marketInstruments.currency),
          _.typeInstruments   -> lift(marketInstruments.typeInstruments),
          _.lot               -> lift(marketInstruments.lot),
          _.ticker            -> lift(marketInstruments.ticker),
          _.isin              -> lift(marketInstruments.isin)
        )
      )
}

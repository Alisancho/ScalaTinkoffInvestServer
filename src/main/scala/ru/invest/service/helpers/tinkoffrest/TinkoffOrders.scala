package ru.invest.service.helpers.tinkoffrest

import monix.eval.Task
import ru.tinkoff.invest.openapi.models.orders.{LimitOrder, PlacedOrder}

trait TinkoffOrders extends Tinkoff {
  val accountId: String

  /**
   * Размещение лимитной заявки.
   *
   * @param figi
   * @param limitOrder
   * @return
   */
  def limitOrders(figi: String, limitOrder: LimitOrder): Task[PlacedOrder] = Task.fromFuture {
    toScala(api.getOrdersContext.placeLimitOrder(figi, limitOrder, accountId))
  }
}

package ru.invest.service.helpers.tinkoffrest

import monix.eval.Task
import ru.tinkoff.invest.openapi.models.market.InstrumentsList

trait TinkoffMarket extends Tinkoff {

  /**
    * Получение списка акций
    */
  def getMarketStocks: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketStocks)
  }

  /**
    * Получение списка ETF
    */
  def getMarketEtfs: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketEtfs)
  }

  /**
    * Получение списка облигаций
    */
  def getMarketBonds: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketBonds)
  }

  /**
    * Получение списка валютных пар
    */
  def getMarketCurrencies: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketCurrencies)
  }
}
package ru.invest.entity.database

import ru.tinkoff.invest.openapi.models.market.Instrument

case class TinkoffToolsTbl(figi: String,
                           name: String,
                           currency: String,
                           ticker: String,
                           isin: String,
                           instruments_type: String,
                           lot: Int) extends BDInvest

object TinkoffToolsTbl {
  implicit def convertToTinkoffTools(instrument: Instrument): TinkoffToolsTbl = TinkoffToolsTbl(
    instrument.figi,
    instrument.name,
    instrument.currency.name(),
    instrument.ticker,
    instrument.isin,
    instrument.`type`.name(),
    instrument.lot
  )
}

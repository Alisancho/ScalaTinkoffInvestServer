package ru.invest.service.helpers.database

import ru.tinkoff.invest.openapi.models.market.Instrument

case class TinkoffTools(figi: String,
                        name: String,
                        currency: String,
                        ticker: String,
                        isin: String,
                        instruments_type: String,
                        lot: Int)

object TinkoffTools {
  implicit def convertToTinkoffTools(instrument: Instrument): TinkoffTools =  TinkoffTools(
    instrument.figi,
    instrument.name,
    instrument.currency.name(),
    instrument.ticker,
    instrument.isin,
    instrument.`type`.name(),
    instrument.lot
  )
}
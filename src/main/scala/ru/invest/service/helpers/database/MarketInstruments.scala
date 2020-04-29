package ru.invest.service.helpers.database

import java.math.BigDecimal

import ru.tinkoff.invest.openapi.models.market.Instrument

case class MarketInstruments(figi: String,
                             name: String,
                             currency: String,
                             typeInstruments: String,
                             lot: Int,
                             ticker: String,
                             isin: String)

 object MarketInstruments{
  implicit def convertFFF(ins: Instrument): MarketInstruments =
    MarketInstruments(ins.figi, ins.name, ins.currency.name(), ins.`type`.name(), ins.lot, ins.ticker, ins.isin)

}
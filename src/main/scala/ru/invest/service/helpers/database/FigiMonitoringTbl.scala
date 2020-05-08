package ru.invest.service.helpers.database

import ru.tinkoff.invest.openapi.models.market.Instrument

case class FigiMonitoringTbl(figi: String, name: String)

object FigiMonitoringTbl {
  implicit def convertToTinkoffTools(instrument: Instrument): FigiMonitoringTbl = FigiMonitoringTbl(
    instrument.figi,
    instrument.name
  )
}

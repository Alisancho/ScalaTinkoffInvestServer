package ru.invest.service.helpers.database

import ru.tinkoff.invest.openapi.models.market.Instrument

case class FigiMonitoring(figi: String, name: String)

object FigiMonitoring {
  implicit def convertToTinkoffTools(instrument: Instrument): FigiMonitoring = FigiMonitoring(
    instrument.figi,
    instrument.name
  )
}

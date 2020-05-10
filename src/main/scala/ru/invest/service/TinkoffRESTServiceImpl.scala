package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import ru.invest.service.helpers.tinkoffrest.{TinkoffMarket, TinkoffOrders, TinkoffPortfolio}
import ru.tinkoff.invest.openapi.OpenApi

class TinkoffRESTServiceImpl(val api: OpenApi, val accountId: String)
    extends TinkoffMarket with TinkoffOrders with TinkoffPortfolio with LazyLogging {
}

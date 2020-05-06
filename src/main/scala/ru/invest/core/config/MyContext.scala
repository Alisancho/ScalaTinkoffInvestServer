package ru.invest.core.config

import java.time.Instant
import java.util.Date

import io.getquill.{MysqlMonixJdbcContext, SnakeCase}

class MyContext extends MysqlMonixJdbcContext(SnakeCase, "ctx") {
  lazy implicit val instantEncoder: MappedEncoding[Instant, Date] = MappedEncoding[Instant, Date] { i =>
    new Date(i.toEpochMilli)
  }
  lazy implicit val instantDecoder: MappedEncoding[Date, Instant] = MappedEncoding[Date, Instant] { d =>
    Instant.ofEpochMilli(d.getTime)
  }
}
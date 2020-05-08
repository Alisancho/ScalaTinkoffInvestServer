package ru.invest.service.helpers.tinkoffrest

import java.util.concurrent.{CompletionStage, Executors}

import ru.invest.service.helpers.subscriber.MainSubscriber
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory

import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

trait Tinkoff {
  val factory: OkHttpOpenApiFactory
  val api: OpenApi
  val listener: MainSubscriber
  def toScala[T](cs: CompletionStage[T]): Future[T] = {
    cs match {
      case cf: CF[T] => cf.wrapped
      case _ =>
        val p = new P[T](cs)
        cs whenComplete p
        p.future
    }
  }
}

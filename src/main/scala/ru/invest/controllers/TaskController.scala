package ru.invest.controllers
import akka.http.scaladsl.server.Directives.{complete, path, pathPrefix, _}
import akka.http.scaladsl.server.{Directives, Route}
import ru.invest.service.{DataBaseServiceImpl, TinkoffRESTServiceImpl}

import scala.language.postfixOps

class TaskController(tinkoffRESTServiceImpl: TinkoffRESTServiceImpl, dataBaseServiceImpl: DataBaseServiceImpl) {
  def routApiV1: Route =
    pathPrefix("api" / "v1") {
      path("version") {
        get {
          complete("version=2.4")
        }
      } ~ path("startMonitoring") {
        post {
          entity(as[String]) { p =>
            complete("OK")
          }
        }
      }
    }
}

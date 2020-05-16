package ru.invest.controllers
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives.{complete, path, pathPrefix, _}
import akka.http.scaladsl.server.{Directives, Route}
import monix.execution.schedulers.SchedulerService
import ru.invest.service.{BusinessProcessServiceImpl, DataBaseServiceImpl, TinkoffRESTServiceImpl}

import scala.language.postfixOps

class TaskController(businessProcessServiceImpl: BusinessProcessServiceImpl)(schedulerTinkoff: SchedulerService) {
  def routApiV1: Route =
    pathPrefix("api" / "v1") {
      path("version") {
        get {
          complete("version=2.4")
        }
      } ~ path("startMonitoring") {
        get {
          entity(as[HttpRequest]) { p =>
           complete("")
          }
        }
      }
    }
}

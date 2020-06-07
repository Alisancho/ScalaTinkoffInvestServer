package ru.invest.controllers

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives.{complete, path, pathPrefix, _}
import akka.http.scaladsl.server.{Directives, Route}

import scala.language.postfixOps

class TaskController {
  def routApiV1(): Route =
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
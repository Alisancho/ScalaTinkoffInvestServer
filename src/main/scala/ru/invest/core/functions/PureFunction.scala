package ru.invest.core.functions

import java.util.UUID

import monix.eval.Task

object PureFunction {

  def getUUID: Task[String] = Task {
    "5-" + UUID
      .randomUUID()
      .toString
      .replace("-", "")
      .substring(23)
  }

  def getUUIDString: String = "3-" + UUID.randomUUID()
      .toString
      .replace("-", "")
      .substring(23)

}

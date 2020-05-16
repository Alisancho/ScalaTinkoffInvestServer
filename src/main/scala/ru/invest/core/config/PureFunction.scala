package ru.invest.core.config

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
}

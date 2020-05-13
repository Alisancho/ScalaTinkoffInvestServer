package ru.invest.service

import monix.catnap.MVar
import monix.eval.Task
import ru.invest.service.helpers.database.TaskMonitoringTbl

object MVarServiceImpl {
  def apply(l: List[TaskMonitoringTbl]): Task[MVar[Task, List[TaskMonitoringTbl]]] =
    for {
      state <- MVar[Task].of(l)
    } yield state
}

package ru.invest.service.helpers.database


trait DBTable


trait TCInvestTools[F[_]] {
  def insertToDB(sop:DBTable): F[Long]
}

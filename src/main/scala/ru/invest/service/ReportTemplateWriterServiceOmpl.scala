//package ru.invest.service
//
//import java.io.FileOutputStream
//
//import org.apache.poi.ss.util.CellRangeAddress
//import org.apache.poi.xssf.usermodel.{XSSFRow, XSSFSheet, XSSFWorkbook}
//
//object ReportTemplateWriterServiceOmpl {
//  val months: Array[String] = Array("октябрь 2012", "ноябрь 2012", "декабрь 2012",
//    "январь 2013", "февраль 2013", "март 2013")
//  val data = Map(
//    (1,"RU-CENTER") -> Array(83318, 80521, 83048, 73638, 82014, 93982),
//    (2,"REGRU") -> Array(35621, 37013, 36515, 41595, 45042, 49101),
//    (3,"R01") -> Array(44155, 44356, 43199, 39629, 42754, 48528),
//    (4,"REGTIME") -> Array(19999, 18587, 18630, 18627, 19886, 20496)
//  )
//
//
//  // Открываем шаблон отчета
//  val wb = new XSSFWorkbook(
//    getClass.getResourceAsStream("/template.xlsx")
//  )
//  val sheet: XSSFSheet = wb.getSheetAt(0)
//
//  // Заполняем шапку таблицы
//  val headerRow: XSSFRow = sheet.getRow(0)
//  for(idx <- 1 to months.size) {
//    headerRow.getCell(idx).setCellValue(months(idx - 1))
//  }
//
//  // Заполняем тело таблицы
//  for(key @ (rowNumber, rowName) <- data.keys) {
//    val row = sheet.getRow(rowNumber)
//    row.getCell(0).setCellValue(rowName)
//    for(idx <- 1 to data(key).size) {
//      row.getCell(idx).setCellValue(data(key)(idx - 1))
//    }
//  }
//
//  // Также нужно заполнить подвал, иначе он не пересчитается
//  val footerRow: XSSFRow = sheet.getRow(data.size + 1)
//  for(idx <- 1 to months.size) {
//    val cell = footerRow.getCell(idx)
//    val range = new CellRangeAddress(1, data.size, idx, idx)
//    cell.setCellFormula(s"SUM(${range.formatAsString})")
//  }
//
//  // Сохраняем отчет
//  val resultFile = new FileOutputStream("report.xlsx")
//  wb.write(resultFile)
//  resultFile.close
//}

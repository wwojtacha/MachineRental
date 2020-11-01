package machineRental.MR.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class ExcelReportGenerator {

  public ByteArrayInputStream exportExcelReport(LocalDate startDate, LocalDate endDate, String sheetName) throws IOException {

    XSSFWorkbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet(sheetName);

    writeHeaderLine(sheet);

    writeDataLines(startDate, endDate, sheet);

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    workbook.write(out);

    return new ByteArrayInputStream(out.toByteArray());
  }

  abstract void writeHeaderLine(Sheet sheet);

  abstract void writeDataLines(LocalDate startDate, LocalDate endDate, Sheet sheet);

  Date convertToDateViaSqlDate(LocalDate dateToConvert) {
    return java.sql.Date.valueOf(dateToConvert);
  }

}

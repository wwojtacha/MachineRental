package machineRental.MR.reports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelReportGenerator {

  public ByteArrayInputStream exportExcelReport(LocalDate startDate, LocalDate endDate) throws IOException {
    return new ByteArrayInputStream(null);
  }

  void writeHeaderLine(Sheet sheet) {};

  void writeDataLines(LocalDate startDate, LocalDate endDate, Sheet sheet) {};

  Date convertToDateViaSqlDate(LocalDate dateToConvert) {
    return java.sql.Date.valueOf(dateToConvert);
  }

}

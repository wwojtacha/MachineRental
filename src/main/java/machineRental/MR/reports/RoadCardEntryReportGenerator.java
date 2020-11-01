package machineRental.MR.reports;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadCardEntryReportGenerator extends ExcelReportGenerator{

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  @Override
  void writeHeaderLine(Sheet sheet) {
    Row headerRow = sheet.createRow(0);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("Document number");

    headerCell = headerRow.createCell(1);
    headerCell.setCellValue("Document type");

    headerCell = headerRow.createCell(2);
    headerCell.setCellValue("Date");

    headerCell = headerRow.createCell(3);
    headerCell.setCellValue("Machine id");

    headerCell = headerRow.createCell(4);
    headerCell.setCellValue("Operator name");

    headerCell = headerRow.createCell(5);
    headerCell.setCellValue("Delegation");

    headerCell = headerRow.createCell(6);
    headerCell.setCellValue("Invoice number");

    headerCell = headerRow.createCell(7);
    headerCell.setCellValue("Work code");

    headerCell = headerRow.createCell(8);
    headerCell.setCellValue("Start hour");

    headerCell = headerRow.createCell(9);
    headerCell.setCellValue("End hour");

    headerCell = headerRow.createCell(10);
    headerCell.setCellValue("Loading place");

    headerCell = headerRow.createCell(11);
    headerCell.setCellValue("Material");

    headerCell = headerRow.createCell(12);
    headerCell.setCellValue("Unloading place");

    headerCell = headerRow.createCell(13);
    headerCell.setCellValue("Quantity");

    headerCell = headerRow.createCell(14);
    headerCell.setCellValue("Measure unit");

    headerCell = headerRow.createCell(15);
    headerCell.setCellValue("Distance");

    headerCell = headerRow.createCell(16);
    headerCell.setCellValue("Price type");

    headerCell = headerRow.createCell(17);
    headerCell.setCellValue("Price");

    headerCell = headerRow.createCell(18);
    headerCell.setCellValue("Estimate name");

    headerCell = headerRow.createCell(19);
    headerCell.setCellValue("Estimate cost code");

    headerCell = headerRow.createCell(20);
    headerCell.setCellValue("Cost code (sell)");

    headerCell = headerRow.createCell(21);
    headerCell.setCellValue("Accepted by");

  }

  @Override
  void writeDataLines(LocalDate startDate, LocalDate endDate, Sheet sheet) {

    List<RoadCardEntry> roadCardEntries = roadCardEntryService.getRoadCardEntriesBetweenDates(startDate, endDate);

    int rowNumber = 1;

    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      Row row = sheet.createRow(rowNumber);

      Cell rowCell = row.createCell(0);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getId());

      rowCell = row.createCell(1);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getDocumentType().name());

      rowCell = row.createCell(2);
      rowCell.setCellValue(convertToDateViaSqlDate(roadCardEntry.getWorkDocument().getDate()));

      rowCell = row.createCell(3);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getMachine().getInternalId());

      rowCell = row.createCell(4);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getOperator().getName());

      rowCell = row.createCell(5);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getDelegation());

      rowCell = row.createCell(6);
      rowCell.setCellValue(roadCardEntry.getWorkDocument().getInvoiceNumber());

      rowCell = row.createCell(7);
      rowCell.setCellValue(roadCardEntry.getWorkCode().toString());

      rowCell = row.createCell(8);
      rowCell.setCellValue(roadCardEntry.getStartHour().toString());

      rowCell = row.createCell(9);
      rowCell.setCellValue(roadCardEntry.getEndHour().toString());

      rowCell = row.createCell(10);
      rowCell.setCellValue(roadCardEntry.getLoadingPlace());

      rowCell = row.createCell(11);
      rowCell.setCellValue(roadCardEntry.getMaterial());

      rowCell = row.createCell(12);
      rowCell.setCellValue(roadCardEntry.getUnloadingPlace());

      rowCell = row.createCell(13);
      rowCell.setCellValue(roadCardEntry.getQuantity());

      rowCell = row.createCell(14);
      rowCell.setCellValue(roadCardEntry.getMeasureUnit());

      rowCell = row.createCell(15);
      rowCell.setCellValue(roadCardEntry.getDistance());

      rowCell = row.createCell(16);
      rowCell.setCellValue(roadCardEntry.getDistancePrice().getPriceType().name());

      rowCell = row.createCell(17);
      rowCell.setCellValue(roadCardEntry.getDistancePrice().getPrice().doubleValue());

      rowCell = row.createCell(18);
      rowCell.setCellValue(roadCardEntry.getEstimatePosition().getName());

      rowCell = row.createCell(19);
      rowCell.setCellValue(roadCardEntry.getEstimatePosition().getCostCode().getFullCode());

      rowCell = row.createCell(20);
      rowCell.setCellValue(roadCardEntry.getCostCode().getFullCode());

      rowCell = row.createCell(21);
      rowCell.setCellValue(roadCardEntry.getAcceptingPerson().getName());

      rowNumber++;

    }
  }

}

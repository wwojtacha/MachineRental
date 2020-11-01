package machineRental.MR.reports;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryDocumentEntryReportGenerator extends ExcelReportGenerator{

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

  @Override
  void writeHeaderLine(Sheet sheet) {
    Row headerRow = sheet.createRow(0);

    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("Document number (contractor)");

    headerCell = headerRow.createCell(1);
    headerCell.setCellValue("Document number (number)");

    headerCell = headerRow.createCell(2);
    headerCell.setCellValue("Date");

    headerCell = headerRow.createCell(3);
    headerCell.setCellValue("Entry contractor");

    headerCell = headerRow.createCell(4);
    headerCell.setCellValue("Material");

    headerCell = headerRow.createCell(5);
    headerCell.setCellValue("Quantity");

    headerCell = headerRow.createCell(6);
    headerCell.setCellValue("Measure unit");

    headerCell = headerRow.createCell(7);
    headerCell.setCellValue("Price type");

    headerCell = headerRow.createCell(8);
    headerCell.setCellValue("Price");

    headerCell = headerRow.createCell(9);
    headerCell.setCellValue("Cost value");

    headerCell = headerRow.createCell(10);
    headerCell.setCellValue("Estimate name");

    headerCell = headerRow.createCell(11);
    headerCell.setCellValue("Estimate cost code");

    headerCell = headerRow.createCell(12);
    headerCell.setCellValue("Cost code (sell)");

    headerCell = headerRow.createCell(13);
    headerCell.setCellValue("Invoice number");

  }

  @Override
  void writeDataLines(LocalDate startDate, LocalDate endDate, Sheet sheet) {

    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryService.getDeliveryDocumentEntriesBetweenDates(startDate, endDate);

    int rowNumber = 1;

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {
      Row row = sheet.createRow(rowNumber);

      Cell rowCell = row.createCell(0);
      rowCell.setCellValue(deliveryDocumentEntry.getDeliveryDocument().getContractor().getMpk());

      rowCell = row.createCell(1);
      rowCell.setCellValue(deliveryDocumentEntry.getDeliveryDocument().getDocumentNumber());

      rowCell = row.createCell(2);
      rowCell.setCellValue(convertToDateViaSqlDate(deliveryDocumentEntry.getDeliveryDocument().getDate()));

      rowCell = row.createCell(3);
      rowCell.setCellValue(deliveryDocumentEntry.getContractor().getMpk());

      rowCell = row.createCell(4);
      rowCell.setCellValue(deliveryDocumentEntry.getMaterial().getType());

      rowCell = row.createCell(5);
      rowCell.setCellValue(deliveryDocumentEntry.getQuantity());

      rowCell = row.createCell(6);
      rowCell.setCellValue(deliveryDocumentEntry.getMeasureUnit());

      rowCell = row.createCell(7);
      rowCell.setCellValue(deliveryDocumentEntry.getDeliveryPrice().getPriceType().name());

      rowCell = row.createCell(8);
      double price = deliveryDocumentEntry.getDeliveryPrice().getPrice().doubleValue();
      rowCell.setCellValue(price);

      rowCell = row.createCell(9);
      rowCell.setCellValue(deliveryDocumentEntry.getQuantity() * price);

      rowCell = row.createCell(10);
      rowCell.setCellValue(deliveryDocumentEntry.getEstimatePosition().getName());

      rowCell = row.createCell(11);
      rowCell.setCellValue(deliveryDocumentEntry.getEstimatePosition().getCostCode().getFullCode());

      rowCell = row.createCell(12);
      rowCell.setCellValue(deliveryDocumentEntry.getCostCode().getFullCode());

      rowCell = row.createCell(13);
      rowCell.setCellValue(deliveryDocumentEntry.getInvoiceNumber());

      rowNumber++;

    }
  }

}

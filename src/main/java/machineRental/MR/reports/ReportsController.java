package machineRental.MR.reports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportsController {

  @Autowired
  private WorkReportEntryReportGenerator workReportEntryReportGenerator;

  @Autowired
  private RoadCardEntryReportGenerator roadCardEntryReportGenerator;

  @Autowired
  private DeliveryDocumentEntryReportGenerator deliveryDocumentEntryReportGenerator;


  @GetMapping("/workReportEntriesReport")
  public ResponseEntity<InputStreamResource> exportExcelWorkReportEntryReport(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "startDate") LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "endDate") LocalDate endDate) throws IOException {

    String sheetName = "WorkReportEntries";

    try (ByteArrayInputStream in = workReportEntryReportGenerator.exportExcelReport(startDate, endDate, sheetName)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=workReportEntries.xlsx");
      headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

      return ResponseEntity
          .ok()
          .headers(headers)
          .body(new InputStreamResource(in));
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to export data to Excel file" + ioe.getMessage());
    }
  }

  @GetMapping("/roadCardEntriesReport")
  public ResponseEntity<InputStreamResource> exportExcelRoadCardEntryReport(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "startDate") LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "endDate") LocalDate endDate) throws IOException {

    String sheetName = "RoadCardEntries";

    try (ByteArrayInputStream in = roadCardEntryReportGenerator.exportExcelReport(startDate, endDate, sheetName)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=roadCardEntries.xlsx");
      headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

      return ResponseEntity
          .ok()
          .headers(headers)
          .body(new InputStreamResource(in));
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to export data to Excel file" + ioe.getMessage());
    }
  }

  @GetMapping("/deliveryDocumentEntriesReport")
  public ResponseEntity<InputStreamResource> exportExcelDeliveryDocumentEntryReport(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "startDate") LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "endDate") LocalDate endDate) throws IOException {

    String sheetName = "DelivertDocumentEntries";

    try (ByteArrayInputStream in = deliveryDocumentEntryReportGenerator.exportExcelReport(startDate, endDate, sheetName)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=deliveryDocumentEntries.xlsx");
      headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

      return ResponseEntity
          .ok()
          .headers(headers)
          .body(new InputStreamResource(in));
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to export data to Excel file" + ioe.getMessage());
    }
  }
}

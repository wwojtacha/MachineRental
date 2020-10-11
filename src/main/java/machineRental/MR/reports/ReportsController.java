package machineRental.MR.reports;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/reports")
public class ReportsController {

  @Autowired
  private ReportService reportService;

//  @Autowired
//  private WorkDocumentService workDocumentService;
//
//  @PostMapping
//  @ResponseStatus(HttpStatus.CREATED)
//  public WorkDocument create(@RequestBody @Valid WorkDocument workDocument, BindingResult bindingResult) {
//    return workDocumentService.create(workDocument, bindingResult);
//  }

//  @GetMapping
//  @ResponseStatus(HttpStatus.OK)
//  public void search(
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "startDate") LocalDate startDate,
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "endDate") LocalDate endDate) {
//
//    reportService.generateExcelReport(startDate, endDate);
//  }

//  @GetMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public WorkDocument getById(@PathVariable String id) {
//    return workDocumentService.getById(id);
//  }
//
//  @PutMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public WorkDocument update(@PathVariable String id, @RequestBody @Valid WorkDocument workDocument, BindingResult bindingResult) {
//    return workDocumentService.update(id, workDocument, bindingResult);
//  }
//
//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id) {
//    workDocumentService.delete(id);
//  }

  @GetMapping("/workReportEntriesReport")
  @ResponseStatus(HttpStatus.OK)
  public List<WorkReportEntry> generateReport(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "startDate") LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "endDate") LocalDate endDate) {

    return reportService.generateReport(startDate, endDate);
  }

//  @GetMapping("/workReportEntriesReport")
//  public void exportToExcel(HttpServletResponse response,
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "startDate") LocalDate startDate,
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "endDate") LocalDate endDate) throws IOException {
//    response.setContentType("application/octet-stream");
//    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//    String currentDateTime = dateFormatter.format(new Date());
//
//    String headerKey = "Content-Disposition";
//    String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
//    response.setHeader(headerKey, headerValue);
//
//    reportService.export(response, startDate, endDate);
//  }

}

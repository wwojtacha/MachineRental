package machineRental.MR.reports.cost;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costReport")
public class CostReportController {

  @Autowired
  private CostReportService costReportService;

//  @GetMapping
//  @ResponseStatus(HttpStatus.OK)
//  public List<TotalEquipmentCost> getEquipmentCost(
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "startDate") LocalDate startDate,
//      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//      @RequestParam(name = "endDate") LocalDate endDate,
//      @RequestParam(name = "projectCode") String projectCode) {
//
//    return costReportService.getTotalEquipmentCostByEstimatePosition(startDate, endDate, projectCode);
//  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<CostReport> getCostReports(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "startDate") LocalDate startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "endDate") LocalDate endDate,
      @RequestParam(name = "projectCode") String projectCode) {

    return costReportService.getCostReports(startDate, endDate, projectCode);
  }

}

package machineRental.MR.dailyReport;

import java.time.LocalDate;
import javax.validation.Valid;
import machineRental.MR.delivery.document.model.DeliveryDocumentDto;
import machineRental.MR.estimate.model.EstimatePosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dailyReports")
public class DailyReportController {

  @Autowired
  private DailyReportService dailyReportService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DailyReportDto create (@RequestBody @Valid DailyReportDto dailyReportDto, BindingResult bindingResult) {
    return dailyReportService.create(dailyReportDto, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<DailyReportDto> search(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(value = "date", required = false) LocalDate date,
      @RequestParam(name = "estimatePositionName", required = false, defaultValue = "") String estimatePositionName,
      @RequestParam(name = "estimatePositionCostCode", required = false, defaultValue = "") String estimatePositionCostCode,
//      EstimatePosition estimatePosition,
      @RequestParam(name = "location", required = false, defaultValue = "") String location,
      Pageable pageable) {

    return dailyReportService.search(date, estimatePositionName, estimatePositionCostCode, location, pageable);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DailyReportDto update(@PathVariable Long id, @RequestBody @Valid DailyReportDto dailyReportDto, BindingResult bindingResult) {
    return dailyReportService.update(id, dailyReportDto, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    dailyReportService.delete(id);
  }

}

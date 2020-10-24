package machineRental.MR.dailyReport;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import machineRental.MR.estimate.model.EstimatePosition;

@Data
@AllArgsConstructor
public class DailyReportDto {

  private Long id;

  private LocalDate date;

  private EstimatePosition estimatePosition;

  private String location;

  private String startPoint;

  private String endPoint;

  private String side;

  private double quantity;

  private String measureUnit;

  private String remarks;

}

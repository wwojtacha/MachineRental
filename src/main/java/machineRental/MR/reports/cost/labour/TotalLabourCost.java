package machineRental.MR.reports.cost.labour;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.reports.cost.transport.TransportCost;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalLabourCost {

//  private EstimatePosition estimatePosition;
  private double totalWorkHoursCount;
  private BigDecimal totalCostValue;


}

package machineRental.MR.reports.cost.transport;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalTransportCost {

//  private EstimatePosition estimatePosition;
  private List<TransportCost> transportCosts;
  private double totalWorkHoursCount;
  private BigDecimal totalCostValue;


}

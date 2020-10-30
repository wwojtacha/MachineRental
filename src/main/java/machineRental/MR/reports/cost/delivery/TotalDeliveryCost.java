package machineRental.MR.reports.cost.delivery;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.reports.cost.transport.TransportCost;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalDeliveryCost {

//  private EstimatePosition estimatePosition;
  private List<DeliveryCost> deliveryCosts;
  private BigDecimal totalCostValue;


}

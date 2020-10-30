package machineRental.MR.reports.cost;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.reports.cost.delivery.TotalDeliveryCost;
import machineRental.MR.reports.cost.equipment.TotalEquipmentCost;
import machineRental.MR.reports.cost.transport.TotalTransportCost;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostReport {

  private EstimatePosition estimatePosition;
  private double totalDailyReportQuantity;
  private TotalEquipmentCost totalEquipmentCost;
  private TotalTransportCost totalTransportCost;
  private TotalDeliveryCost totalDeliveryCost;

}

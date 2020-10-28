package machineRental.MR.reports.cost.equipment;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalEquipmentCost {

//  private EstimatePosition estimatePosition;
  private List<EquipmentCost> equipmentCosts;
  private double totalWorkHoursCount;
  private BigDecimal totalCostValue;


}

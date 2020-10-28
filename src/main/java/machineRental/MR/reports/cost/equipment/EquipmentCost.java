package machineRental.MR.reports.cost.equipment;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.machineType.model.MachineType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentCost {

//  private EstimatePosition estimatePosition;
  private MachineType machineType;
  private double workHoursCount;
  private BigDecimal costValue;


//  public EquipmentCost(final double workHoursCount, final BigDecimal costValue) {
//    this.workHoursCount = workHoursCount;
//    this.costValue = costValue;
//  }
}

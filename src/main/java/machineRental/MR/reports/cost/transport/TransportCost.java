package machineRental.MR.reports.cost.transport;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.machineType.model.MachineType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportCost {

//  private EstimatePosition estimatePosition;
  private MachineType machineType;
  private double workHoursCount;
  private BigDecimal costValue;

}

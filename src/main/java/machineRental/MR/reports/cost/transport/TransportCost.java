package machineRental.MR.reports.cost.transport;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.price.PriceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportCost {

//  private EstimatePosition estimatePosition;
  private MachineType machineType;
  private PriceType priceType;
  private double workHoursCount;
  private double distanceCount;
  private double quantityCount;
  private BigDecimal costValue;

}

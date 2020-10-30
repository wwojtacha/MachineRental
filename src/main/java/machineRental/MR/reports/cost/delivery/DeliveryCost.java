package machineRental.MR.reports.cost.delivery;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.PriceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryCost {

//  private EstimatePosition estimatePosition;
  private Material material;
  private PriceType priceType;
  private double quantityCount;
  private BigDecimal costValue;
}

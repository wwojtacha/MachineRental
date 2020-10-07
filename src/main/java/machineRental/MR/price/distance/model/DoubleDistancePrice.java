package machineRental.MR.price.distance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.price.hour.model.HourPrice;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoubleDistancePrice {

  private DistancePrice editedDistancePrice;
  private DistancePrice newDistancePrice;

}

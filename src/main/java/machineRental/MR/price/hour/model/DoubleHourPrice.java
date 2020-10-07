package machineRental.MR.price.hour.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoubleHourPrice {

  private HourPrice editedHourPrice;
  private HourPrice newHourPrice;

}

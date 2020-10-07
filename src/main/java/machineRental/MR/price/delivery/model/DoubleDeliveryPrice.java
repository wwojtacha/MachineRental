package machineRental.MR.price.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.price.hour.model.HourPrice;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoubleDeliveryPrice {

  private DeliveryPrice editedDeliveryPrice;
  private DeliveryPrice newDeliveryPrice;

}

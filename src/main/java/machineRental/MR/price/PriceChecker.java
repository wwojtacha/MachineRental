package machineRental.MR.price;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import machineRental.MR.price.exception.PriceAlreadyUsedException;
import machineRental.MR.price.hour.model.HourPrice;

public interface PriceChecker {

  default void checkPriceUsage(Long priceId) {
    if (isPriceAlreadyUsed(priceId)) {
      throw new PriceAlreadyUsedException(String.format("Price with id %s is already used in at least one document.", priceId));
    }
  }

  boolean isPriceAlreadyUsed(Long priceId);

}

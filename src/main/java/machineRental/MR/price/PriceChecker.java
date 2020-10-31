package machineRental.MR.price;

import machineRental.MR.exception.AlreadyUsedException;

public interface PriceChecker {

  default void checkPriceUsage(Long priceId) {
    if (isPriceAlreadyUsed(priceId)) {
      throw new AlreadyUsedException(String.format("Price with id %s is already used in at least one document.", priceId));
    }
  }

  boolean isPriceAlreadyUsed(Long priceId);

}

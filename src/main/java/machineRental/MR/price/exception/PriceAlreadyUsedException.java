package machineRental.MR.price.exception;

public class PriceAlreadyUsedException extends RuntimeException {

  public PriceAlreadyUsedException(final String message) {
    super(message);
  }
}

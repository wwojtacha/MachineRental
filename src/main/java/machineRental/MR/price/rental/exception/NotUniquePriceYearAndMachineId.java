package machineRental.MR.price.rental.exception;

public class NotUniquePriceYearAndMachineId extends RuntimeException {
  public NotUniquePriceYearAndMachineId(final String message) {
    super(message);
  }
}


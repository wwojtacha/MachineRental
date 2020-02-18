package machineRental.MR.price.exception;

public class NotUniquePriceYearAndMachineId extends RuntimeException {
  public NotUniquePriceYearAndMachineId(final String message) {
    super(message);
  }
}


package machineRental.MR.price.hour.exception;

public class OverlappingDatesException extends RuntimeException {
  public OverlappingDatesException(final String message) {
    super(message);
  }
}


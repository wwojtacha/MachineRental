package machineRental.MR.excel;

public class NotPresentInDbException extends RuntimeException {
  public NotPresentInDbException(final String message) {
    super(message);
  }
}

package machineRental.MR.excel;

public class AlreadyInDbException extends RuntimeException {
  public AlreadyInDbException(final String message) {
    super(message);
  }
}

package machineRental.MR.exception;

public class AlreadyUsedException extends RuntimeException {

  public AlreadyUsedException(final String message) {
    super(message);
  }
}

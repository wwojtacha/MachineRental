package machineRental.MR.price.exception;

public class WrongFileTypeException extends RuntimeException{
  public WrongFileTypeException(final String message) {
    super(message);
  }
}

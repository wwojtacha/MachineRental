package machineRental.MR.excel;

public class WrongFileTypeException extends RuntimeException{
  public WrongFileTypeException(final String message) {
    super(message);
  }
}

package machineRental.MR.exception;

public class WrongAuthenticationException extends RuntimeException {

  public WrongAuthenticationException(String message) {
    super(message);
  }

}


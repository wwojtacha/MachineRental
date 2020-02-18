package machineRental.MR.order;

import static java.lang.String.format;

import machineRental.MR.order.model.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public final class ErrorOrder {

  public void noPriceInDbError(Order order, BindingResult bindingResult) {
    String machineInternaId = order.getMachine().getInternalId();
    Integer year = order.getStartDate().getYear();
    bindingResult.addError(new FieldError("price", "id", format("There is no price in data base for machine \'%s\' and year \'%s\'", machineInternaId, year)));
  }

  public void wrongPriceTypeError(BindingResult bindingResult) {
    bindingResult.addError(new FieldError("order", "price", "Price type must be specific to rental time e.g. 8 days require 'Week' price type"));

  }

  public void wrongPriceValueError(BindingResult bindingResult) {
    bindingResult.addError(new FieldError("order", "price", "Price cannot be lower then 0"));

  }

  public void wrongEnteredQuantityError(BindingResult bindingResult) {
    bindingResult.addError(new FieldError("order", "quantity", "Entered quantity cannot be equal or lower than 0."));
  }

  public void insufficientQuantityError(int availableQuantity, Order order, BindingResult bindingResult) {
    int quantity = order.getQuantity();
    bindingResult.addError(new FieldError("machine", "availableQuantity", format("New order quantity \'%s\' is higher than quantity available on stock: \'%s\'.", quantity, availableQuantity)));

  }

  public void wrongDates(BindingResult bindingResult) {
    bindingResult.addError(new FieldError("order", "date", "End date must be equal or greater then start date"));
  }

  public void wrongFebruaryPriceTypeError(BindingResult bindingResult) {
    bindingResult.addError(new FieldError("order", "price", "Whole February was selected. In this case price type must be chosen as \'Month\'."));
  }
}

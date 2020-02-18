package machineRental.MR.order.service;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.DeleteException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.order.model.Order;
import machineRental.MR.price.model.Price;
import machineRental.MR.repository.OrderRepository;
import machineRental.MR.repository.PriceRepository;
import machineRental.MR.order.ErrorOrder;
import machineRental.MR.order.OrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class OrderService {

  private static final int DAYS_OF_RENTAL_MAGNIFIER = 1;

  private static final long CREATION_MODE_ORDER_ID = -1;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PriceRepository priceRepository;

  @Autowired
  private OrderValidator orderValidator;

  private ErrorOrder errorOrder = new ErrorOrder();

  public Order create(Order order, BindingResult bindingResult) {

    orderValidator.triggerVadlidators(order, CREATION_MODE_ORDER_ID, bindingResult);

    setOrderDetails(order, bindingResult);

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }

    return orderRepository.save(order);
  }


  private void setOrderDetails(Order order, BindingResult bindingResult) {
    String priceType = order.getPriceType();

    if (isCustomPriceType(priceType)) {
      setOrderDetailsForCustomPriceType(order, bindingResult);
    } else if (!isCustomPriceType(priceType) && isPriceInDb(order)) {
      setOrderDetailsForPredefinedPriceTypes(order, bindingResult);
    } else {
      errorOrder.noPriceInDbError(order, bindingResult);
    }
  }

  private boolean isCustomPriceType(String priceType) {
    return "Custom".equals(priceType);
  }

  private void setOrderDetailsForCustomPriceType(Order order, BindingResult bindingResult) {
    BigDecimal price = order.getPrice();
    int orderQuantity = order.getQuantity();

    orderValidator.checkUserDefinedPrice(price, bindingResult);

    order.setPrice(price);
    order.setValue(price.multiply(new BigDecimal(orderQuantity)));
    order.setDbPrice(false);
  }

  private boolean isPriceInDb(Order order) {

    Optional<Price> dbPrice = Optional.ofNullable(getPriceFromDb(order));
    return dbPrice.isPresent();
  }

  private void setOrderDetailsForPredefinedPriceTypes(Order order, BindingResult bindingResult) {
    String priceType = order.getPriceType();
    long daysOfRental = ChronoUnit.DAYS.between(order.getStartDate(), order.getEndDate()) + DAYS_OF_RENTAL_MAGNIFIER;
    int orderQuantity = order.getQuantity();
    double monthsOfRental = Math.round(((double) daysOfRental / 30) * 10d) / 10d;
    LocalDate year = order.getStartDate();

    if (isDayPriceType(priceType, daysOfRental)) {
      setOrderDetailsForDayPriceType(order, daysOfRental, orderQuantity);
    } else if (isFebruaryPriceType(order) && isFullFebruary(daysOfRental, year)) {
      setOrderDetailsForFebruaryPriceType(order, orderQuantity, bindingResult);
    } else if (isWeekPriceType(priceType, daysOfRental)) {
      setOrderDetailsForWeekPriceType(order, daysOfRental, orderQuantity);
    } else if (isMonthPriceType(priceType, monthsOfRental)) {
      setOrderDetailForMonthPriceType(order, monthsOfRental, orderQuantity);
    } else {
      errorOrder.wrongPriceTypeError(bindingResult);
    }
  }

  private void setOrderDetailsForDayPriceType(Order order, long daysOfRental, int orderQuantity) {
//    BigDecimal price = getPriceFromDb(order).getPrice();

//    order.setPrice(price);
    BigDecimal price = order.getPrice();
    order.setValue(price.multiply(new BigDecimal(daysOfRental)).multiply(new BigDecimal(orderQuantity)));
    order.setDbPrice(true);
  }

  private void setOrderDetailsForFebruaryPriceType(Order order, int orderQuantity, BindingResult bindingResult) {

    if (!"Month".equals(order.getPriceType())) {
      errorOrder.wrongFebruaryPriceTypeError(bindingResult);
    }

//    BigDecimal price = getPriceFromDb(order).getPrice();
//    order.setPrice(price);
    BigDecimal price = order.getPrice();
    order.setValue(price.multiply(new BigDecimal(orderQuantity)));
    order.setDbPrice(true);
  }

  private void setOrderDetailsForWeekPriceType(Order order, long daysOfRental, int orderQuantity) {
    double weeksOfRental = Math.round(((double) daysOfRental / 7) * 10d) / 10d;
//    BigDecimal price = getPriceFromDb(order).getPrice();
//    order.setPrice(price);
    BigDecimal price = order.getPrice();
    order.setValue(price.multiply(new BigDecimal(weeksOfRental)).multiply(new BigDecimal(orderQuantity)));
    order.setDbPrice(true);
  }

  private void setOrderDetailForMonthPriceType(Order order, double monthsOfRental, int orderQuantity) {
//    BigDecimal price = getPriceFromDb(order).getPrice();
//    order.setPrice(price);

    BigDecimal price = order.getPrice();
    order.setValue(price.multiply(new BigDecimal(monthsOfRental)).multiply(new BigDecimal(orderQuantity)));
    order.setDbPrice(true);
  }

  private Price getPriceFromDb(Order order) {
    Integer priceYear = order.getStartDate().getYear();
    String machineInternalId = String.valueOf(order.getMachine().getId());
    String priceType = order.getPriceType();
    String price = String.valueOf(order.getPrice().setScale(2));

    String priceId = priceYear + machineInternalId + priceType + price;

    Optional<Price> dbPrice = priceRepository.findById(priceId);

    if (!dbPrice.isPresent()) {
      throw new NotFoundException("Price for year: " + priceYear + " and machine internal id: " + machineInternalId + " and price type " + priceType + " does not exist");
    } else {
      return dbPrice.get();
    }
  }

  private boolean isDayPriceType(String priceType, long daysOfRental) {
    return "Day".equals(priceType) && daysOfRental < 7;
  }

  private boolean isFebruaryPriceType(Order order) {
    return order.getStartDate().getYear() == order.getEndDate().getYear()
        && order.getStartDate().getMonthValue() == 2
        && order.getEndDate().getMonthValue() == 2;
  }

  private boolean isFullFebruary(long daysOfRental, LocalDate year) {
    return daysOfRental == 29 || (daysOfRental == 28 && !year.isLeapYear());
  }

  private boolean isWeekPriceType(String priceType, long daysOfRental) {
    return "Week".equals(priceType) && daysOfRental >= 7 && daysOfRental < 30;
  }

  private boolean isMonthPriceType(String priceType, double monthsOfRental) {
    return "Month".equals(priceType) && monthsOfRental >= 1.0;
  }

  public Order update(Long editedOrderId, Order order, BindingResult bindingResult) {
    Optional<Order> dbOrder = orderRepository.findById(editedOrderId);
    if (!dbOrder.isPresent()) {
      throw new NotFoundException(String.format("Order with id \'%s\' doesn`t exist!", editedOrderId));
    }

    orderValidator.triggerVadlidators(order, editedOrderId, bindingResult);

    setOrderDetails(order, bindingResult);

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
    order.setId(editedOrderId);
    return orderRepository.save(order);
  }

  public void delete(Long id) {

    Optional<Order> dbOrder = orderRepository.findById(id);

    if (!dbOrder.isPresent()) {
      throw new NotFoundException(String.format("Order with id \'%s\' doesn`t exist!", id));
    } else if ("Settled".equals(dbOrder.get().getStatus())) {
      throw new DeleteException("It is not allowed to delete order with status 'Settled'!");
    }
    orderRepository.deleteById(id);
  }

  public Page<Order> search(String machineInternalId, String status, LocalDate orderStartDateStart, LocalDate orderStartDateEnd,
      LocalDate orderEndDateStart, LocalDate orderEndDateEnd, String priceType, String clientName, String sellerName, Pageable pageable) {

    LocalDate minDefaultDate = LocalDate.of(1900, 1, 1);
    LocalDate maxDefaultDate = LocalDate.of(2100, 12, 31);

    if (orderStartDateStart == null) {
      orderStartDateStart = minDefaultDate;
    }
    if (orderStartDateEnd == null) {
      orderStartDateEnd = maxDefaultDate;
    }
    if (orderEndDateStart == null) {
      orderEndDateStart = minDefaultDate;
    }
    if (orderEndDateEnd == null) {
      orderEndDateEnd = maxDefaultDate;
    }

    if (status != null && status.isEmpty()) {
      return orderRepository.findByMachine_InternalIdContainingAndStatusContainingAndStartDateBetweenAndEndDateBetweenAndPriceTypeContainingAndClient_NameContainingAndSeller_NameContaining(
          machineInternalId, status, orderStartDateStart, orderStartDateEnd, orderEndDateStart, orderEndDateEnd, priceType, clientName, sellerName, pageable);
    }

    return orderRepository.findByMachine_InternalIdContainingAndStatusEqualsAndStartDateBetweenAndEndDateBetweenAndPriceTypeContainingAndClient_NameContainingAndSeller_NameContaining(
        machineInternalId, status, orderStartDateStart, orderStartDateEnd, orderEndDateStart, orderEndDateEnd, priceType, clientName, sellerName, pageable);

  }

  public Order getById(Long id) {
    Optional<Order> dbOrder = orderRepository.findById(id);

    if (!dbOrder.isPresent()) {
      throw new NotFoundException(String.format("Order with id: \'%s\' does not exist", id));
    }

    return dbOrder.get();
  }
}

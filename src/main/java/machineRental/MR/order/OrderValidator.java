package machineRental.MR.order;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.order.model.Order;
import machineRental.MR.repository.ClientRepository;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OrderRepository;
import machineRental.MR.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class OrderValidator {

  @Autowired
  private SellerRepository sellerRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private MachineRepository machineRepository;

  @Autowired
  private OrderRepository orderRepository;

  private ErrorOrder errorOrder = new ErrorOrder();

  public void triggerVadlidators(Order order, Long editedOrderId, BindingResult bindingResult) {
    validateDates(order, bindingResult);
    validateSeller(order);
    validateClient(order);
    validateMachineInternalId(order);
    checkUserDefinedQuantity(order, bindingResult);
    checkAvailableQuantity(order, editedOrderId, bindingResult);
  }

  private void validateDates(Order order, BindingResult bindingResult) {
    LocalDate startDate = order.getStartDate();
    LocalDate endDate = order.getEndDate();

    if (endDate.isBefore(startDate)) {
      errorOrder.wrongDates(bindingResult);
    }
  }

  private void validateSeller(Order order) {

    String mpk = order.getSeller().getMpk();

    if (sellerRepository.existsByMpk(mpk)) {
      Long sellerId = sellerRepository.findByMpk(mpk).getId();
      order.getSeller().setId(sellerId);
    } else {
      throw new NotFoundException(format("Seller with MPK/NIP \'%s\' does not exist.", mpk));
    }
  }

  private void validateClient(Order order) {

    String mpk = order.getClient().getMpk();

    if (clientRepository.existsByMpk(mpk)) {
      Long clientId = clientRepository.findByMpk(mpk).getId();
      order.getClient().setId(clientId);
    } else {
      throw new NotFoundException(format("Client with MPK/NIP \'%s\' does not exist.", mpk));
    }
  }

  private void validateMachineInternalId(Order order) {

    String machineInternalId = order.getMachine().getInternalId();

    if (machineRepository.existsByInternalId(machineInternalId)) {
      Long machineId = machineRepository.findByInternalId(machineInternalId).getId();
      order.getMachine().setId(machineId);
    } else {
      throw new NotFoundException(format("Machine with internal id \'%s\' does not exist.", machineInternalId));
    }
  }

  public void checkUserDefinedQuantity(Order order, BindingResult bindingResult) {
    int userDefinedQuantity = order.getQuantity();
    if (userDefinedQuantity <= 0) {
      errorOrder.wrongEnteredQuantityError(bindingResult);
    }
  }

  private void checkAvailableQuantity(Order order, Long editedOrderId, BindingResult bindingResult) {
    int availableQuantity = getAvailableQuantity(order, editedOrderId);
    if (!isSufficientQuantity(availableQuantity, order)) {
      errorOrder.insufficientQuantityError(availableQuantity, order, bindingResult);
    }
  }

  private int getAvailableQuantity(Order order, Long editedOrderId) {
    int totalQuantity = machineRepository.findByInternalId(order.getMachine().getInternalId()).getTotalPhysicalQuantity();
    int availableQuantity = totalQuantity;
    List<Order> createdOrders = orderRepository.findByMachine_InternalId(order.getMachine().getInternalId());
    for (Order createdOrder : createdOrders) {

      if (checkOrderDatesCoverPast(order, createdOrder) || checkOrderDatesCoverFuture(order, createdOrder) || isCreatedOrderInBetweenNewOrder(order, createdOrder)) {
        availableQuantity -= createdOrder.getQuantity();

        if (createdOrder.getId() == editedOrderId) {
          availableQuantity += createdOrder.getQuantity();
        }
      }

    }
    return availableQuantity;
  }


  private boolean isSufficientQuantity(int availableQuantity, Order order) {
//    int totalQuantity = machineRepository.findByInternalId(order.getMachine().getInternalId()).getTotalPhysicalQuantity();
//    int availableQuantity = totalQuantity;
//    List<Order> createdOrders = orderRepository.findByMachine_InternalId(order.getMachine().getInternalId());
//    for (Order createdOrder : createdOrders) {
//
//      if (checkOrderDatesCoverPast(order, createdOrder) || checkOrderDatesCoverFuture(order, createdOrder) || isCreatedOrderInBetweenNewOrder(order, createdOrder)) {
//        availableQuantity -= createdOrder.getQuantity();
//      }
//
//    }
    return availableQuantity >= order.getQuantity();
  }

  private boolean checkOrderDatesCoverPast(Order newOrder, Order createdOrder) {
    LocalDate newOrderEndDate = newOrder.getEndDate();
    LocalDate createdOrderStartDate = createdOrder.getStartDate();
    LocalDate createdOrderEndDate = createdOrder.getEndDate();

    return newOrderEndDate.isAfter(createdOrderStartDate) && newOrderEndDate.isBefore(createdOrderEndDate) || newOrderEndDate.isEqual(createdOrderEndDate);
  }

  private boolean checkOrderDatesCoverFuture(Order newOrder, Order createdOrder) {
    LocalDate newOrderStartDate = newOrder.getStartDate();
    LocalDate createdOrderStartDate = createdOrder.getStartDate();
    LocalDate createdOrderEndDate = createdOrder.getEndDate();

    return newOrderStartDate.isBefore(createdOrderEndDate) && newOrderStartDate.isAfter(createdOrderStartDate) || newOrderStartDate.isEqual(createdOrderStartDate);
  }

  private boolean isCreatedOrderInBetweenNewOrder(Order newOrder, Order createdOrder) {
    LocalDate newOrderStartDate = newOrder.getStartDate();
    LocalDate newOrderEndDate = newOrder.getEndDate();
    LocalDate createdOrderStartDate = createdOrder.getStartDate();
    LocalDate createdOrderEndDate = createdOrder.getEndDate();

    return newOrderStartDate.isBefore(createdOrderStartDate) && newOrderEndDate.isAfter(createdOrderEndDate);
  }

  public void checkUserDefinedPrice(BigDecimal enteredPrice, BindingResult bindingResult) {
    if (enteredPrice.floatValue() < 0) {
      errorOrder.wrongPriceValueError(bindingResult);
    }
  }


}

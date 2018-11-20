package machineRental.MR.service;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.model.Order;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OrderRepository;
import machineRental.MR.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private MachineRepository machineRepository;

    public Order create(Order order, BindingResult bindingResult) {
        String internalId = order.getMachine().getInternalId();
        if(internalId == null) {
            bindingResult.addError(new FieldError("machine", "internalId", "Machine internal id must not be null"));
        } else if(!machineRepository.existsByInternalId(internalId)) {
            bindingResult.addError(new FieldError("machine", "internalId", String.format("Machine with internal id: %s does not exist", internalId)));
        } else {
            order.getMachine().setId(machineRepository.findByInternalId(internalId).getId());
        }

        if(bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }

        String priceType = order.getPriceType();
        BigDecimal price = order.getPrice();

        if("Day".equals(priceType)) {
            price = priceRepository.findByMachine_InternalIdAndYear(order.getMachine().getInternalId(), order.getStartDate().getYear()).getPricePerDay();
            order.setPrice(price);
        } else if("Week".equals(priceType)) {
            price = priceRepository.findByMachine_InternalIdAndYear(order.getMachine().getInternalId(), order.getStartDate().getYear()).getPriceFor7Days();
            order.setPrice(price);
        } else if("Month".equals(priceType)) {
            price = priceRepository.findByMachine_InternalIdAndYear(order.getMachine().getInternalId(), order.getStartDate().getYear()).getPriceFor30Days();
            order.setPrice(price);
        } else if("Custom".equals(priceType)) {
            order.setPrice(price);
        }
        return orderRepository.save(order);
    }
}

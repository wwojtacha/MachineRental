package machineRental.MR.controller;

import machineRental.MR.model.Order;
import machineRental.MR.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid Order order, BindingResult bindingResult) {
        return orderService.create(order, bindingResult);
    }

}

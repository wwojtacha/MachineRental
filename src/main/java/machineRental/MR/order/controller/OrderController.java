package machineRental.MR.order.controller;

import java.time.LocalDate;
import machineRental.MR.order.model.Order;
import machineRental.MR.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Order> search(@RequestParam(value = "machineInternalId", required = false, defaultValue = "") String machineInternalId,
                              @RequestParam(value = "status", required = false, defaultValue = "") String status,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              @RequestParam(value = "orderStartDateStart", required = false) LocalDate orderStartDateStart,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              @RequestParam(value = "orderStartDateEnd", required = false) LocalDate orderStartDateEnd,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              @RequestParam(value = "orderEndDateStart", required = false) LocalDate orderEndDateStart,
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              @RequestParam(value = "orderEndDateEnd", required = false) LocalDate orderEndDateEnd,
                              @RequestParam(value = "priceType", required = false, defaultValue = "") String priceType,
                              @RequestParam(value = "clientName", required = false, defaultValue = "") String clientName,
                              @RequestParam(value = "sellerName", required = false, defaultValue = "") String sellerName,
                              Pageable pageable
                              )
    {
        return orderService.search(machineInternalId, status, orderStartDateStart, orderStartDateEnd, orderEndDateStart, orderEndDateEnd, priceType, clientName, sellerName, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order update(@PathVariable Long id, @RequestBody @Valid Order order, BindingResult bindingResult) {
        return orderService.update(id, order, bindingResult);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

}

package machineRental.MR.controller;

import machineRental.MR.model.Seller;
import machineRental.MR.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Seller create(@RequestBody @Valid Seller seller, BindingResult bindingResult) {
        return sellerService.create(seller, bindingResult);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Seller> search(
            @RequestParam(name = "mpk", required = false, defaultValue = "") String mpk,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "city", required = false, defaultValue = "") String city,
            Pageable pageable) {
        return sellerService.search(mpk, name, city, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Seller update(@PathVariable Long id, @RequestBody @Valid Seller seller, BindingResult bindingResult) {
        return sellerService.update(id, seller, bindingResult);
    }

}

package machineRental.MR.price.rental.controller;

import machineRental.MR.price.rental.model.RentalPrice;
import machineRental.MR.price.rental.service.RentalPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/prices")
public class RenatlPriceController {

  @Autowired
  private RentalPriceService rentalPriceService;

//    @GetMapping
//    public String home(Model model) {
//
//        model.addAttribute("sellPrice", new Price());
//        List<Price> prices = springReadFileService.findAll();
//        model.addAttribute("prices", prices);
//
//        return "view/prices";
//    }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadFile(@RequestBody MultipartFile file) {
    rentalPriceService.saveDataFromExcelFile(file);
  }

//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id, BindingResult bindingResult) {
//    priceService.delete(id, bindingResult);
//  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable String id) {
    rentalPriceService.delete(id);
  }


  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public RentalPrice update(@PathVariable String id, @RequestBody @Valid RentalPrice rentalPrice, BindingResult bindingResult) {
    return rentalPriceService.update(id, rentalPrice, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<RentalPrice> search(@RequestParam(value = "year", required = false) Integer year,
                            @RequestParam(value = "machineNumber", required = false, defaultValue = "") String machineNumber,
                            @RequestParam(value = "priceType", required = false, defaultValue = "") String priceType,

      Pageable pageable) {
    return rentalPriceService.search(year, machineNumber, priceType, pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public RentalPrice getPrice(@PathVariable String id) {
    return rentalPriceService.getById(id);
  }

}

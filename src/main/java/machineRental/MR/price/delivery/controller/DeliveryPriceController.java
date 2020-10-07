package machineRental.MR.price.delivery.controller;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import machineRental.MR.price.delivery.model.DeliveryPriceDto;
import machineRental.MR.price.delivery.model.DoubleDeliveryPrice;
import machineRental.MR.price.delivery.service.DeliveryPriceService;
import machineRental.MR.price.PriceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/deliveryPrices")
public class DeliveryPriceController {

  @Autowired
  private DeliveryPriceService deliveryPriceService;

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
    deliveryPriceService.saveDataFromExcelFile(file);
  }

//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id, BindingResult bindingResult) {
//    priceService.delete(id, bindingResult);
//  }

//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id) {
//    hourPriceService.delete(id);
//  }


  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DeliveryPriceDto update(@PathVariable Long id, @RequestBody @Valid DeliveryPriceDto deliveryPrice) {
    return deliveryPriceService.update(id, deliveryPrice);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<DeliveryPriceDto> search(@RequestParam(value = "contractorName", required = false, defaultValue = "") String contractorName,
                            @RequestParam(value = "matrialType", required = false, defaultValue = "") String materialType,
                            @RequestParam(value = "priceType", required = false) List<PriceType> priceType,
                            @RequestParam(value = "projectCode", required = false, defaultValue = "") String projectCode,
                            Pageable pageable) {
    return deliveryPriceService.search(contractorName, materialType, priceType, projectCode, pageable);
  }

  @GetMapping("/matchingPrice")
  @ResponseStatus(HttpStatus.OK)
  public List<DeliveryPriceDto> getPrice(
      @RequestParam(value = "contractorMpk") String contractorMpk,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(value = "date") LocalDate date){
    return deliveryPriceService.getMatchingPrices(contractorMpk, date);
  }

  @PutMapping("/editAndSave/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void updateOnCrossChange(
      @PathVariable Long id, @RequestBody @Valid DoubleDeliveryPrice doubleDeliveryPrice) {
    deliveryPriceService.updateOnDoubleChange(id, doubleDeliveryPrice);
  }

}

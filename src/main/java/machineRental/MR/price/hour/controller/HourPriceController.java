package machineRental.MR.price.hour.controller;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.hour.model.DoubleHourPrice;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.price.hour.service.HourPriceService;
import machineRental.MR.workDocumentEntry.WorkCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/hourPrices")
public class HourPriceController {

  @Autowired
  private HourPriceService hourPriceService;

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
    hourPriceService.saveDataFromExcelFile(file);
  }

//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id, BindingResult bindingResult) {
//    priceService.delete(id, bindingResult);
//  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    hourPriceService.delete(id);
  }


  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public HourPrice update(@PathVariable Long id, @RequestBody @Valid HourPrice hourPrice) {
    return hourPriceService.update(id, hourPrice);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<HourPrice> search(@RequestParam(value = "workCode", required = false) List<WorkCode> workCode,
                            @RequestParam(value = "machineNumber", required = false, defaultValue = "") String machineNumber,
                            @RequestParam(value = "priceType", required = false) List<PriceType> priceType, Pageable pageable) {
    return hourPriceService.search(workCode, machineNumber, priceType, pageable);
  }

  @GetMapping("/matchingPrice")
  @ResponseStatus(HttpStatus.OK)
  public List<HourPrice> getPrice(
      @RequestParam(value = "machineNumber") String machineNumber,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(value = "date") LocalDate date) {
    return hourPriceService.getMatchingPrices(machineNumber, date);
  }

//  @PutMapping("/editAndSave")
//  @ResponseStatus(HttpStatus.OK)
//  public void updateOnDoubleChange(
//      @RequestParam(value = "id") String id,
//      @RequestParam(value = "workCode") WorkCode workCode,
//      @RequestParam(value = "machineInternalId") String machineInternalId,
//      @RequestParam(value = "priceType") PriceType priceType,
//      @RequestParam(value = "price") BigDecimal price,
//      @RequestParam(value = "startDate") LocalDate startDate,
//      @RequestParam(value = "endDate") LocalDate endDate,
//      @RequestParam(value = "projectCode") String projectCode,
//      @RequestBody @Valid HourPrice newHourPrice) {
//    hourPriceService.updateOnDoubleChange(id, workCode, machineInternalId, priceType, price, startDate, endDate, projectCode, newHourPrice);
//  }

  @PutMapping("/editAndSave/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void updateOnCrossChange(
      @PathVariable Long id, @RequestBody @Valid DoubleHourPrice doubleHourPrice) {
    hourPriceService.updateOnDoubleChange(id, doubleHourPrice);
  }

//  @GetMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public Price getPrice(@PathVariable String id) {
//    return hourPriceService.getById(id);
//  }

}

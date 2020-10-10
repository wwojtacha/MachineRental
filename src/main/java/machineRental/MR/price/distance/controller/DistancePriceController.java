package machineRental.MR.price.distance.controller;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import machineRental.MR.price.distance.model.DoubleDistancePrice;
import machineRental.MR.price.distance.service.DistancePriceService;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.price.PriceType;
import machineRental.MR.workDocumentEntry.WorkCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/distancePrices")
public class DistancePriceController {

  @Autowired
  private DistancePriceService distancePriceService;

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
    distancePriceService.saveDataFromExcelFile(file);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    distancePriceService.delete(id);
  }

//  @DeleteMapping("/{id}")
//  @ResponseStatus(HttpStatus.OK)
//  public void delete(@PathVariable String id) {
//    hourPriceService.delete(id);
//  }


  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DistancePrice update(@PathVariable Long id, @RequestBody @Valid DistancePrice distancePrice) {
    return distancePriceService.update(id, distancePrice);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<DistancePrice> search(@RequestParam(value = "workCode", required = false) List<WorkCode> workCode,
                            @RequestParam(value = "machineNumber", required = false, defaultValue = "") String machineNumber,
                            @RequestParam(value = "priceType", required = false) List<PriceType> priceType, Pageable pageable) {
    return distancePriceService.search(workCode, machineNumber, priceType, pageable);
  }

  @GetMapping("/matchingPrice")
  @ResponseStatus(HttpStatus.OK)
  public List<DistancePrice> getPrice(
      @RequestParam(value = "machineNumber") String machineNumber,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(value = "date") LocalDate date){
    return distancePriceService.getMatchingPrices(machineNumber, date);
  }

  @PutMapping("/editAndSave/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void updateOnCrossChange(
      @PathVariable Long id, @RequestBody @Valid DoubleDistancePrice doubleDistancePrice) {
    distancePriceService.updateOnDoubleChange(id, doubleDistancePrice);
  }

}

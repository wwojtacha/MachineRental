package machineRental.MR.delivery.entry.controller;

import java.util.List;
import javax.validation.Valid;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntryDto;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
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

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/deliveryDocumentEntries")
public class DeliveryDocumentEntryController {

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DeliveryDocumentEntryDto create (@RequestBody @Valid DeliveryDocumentEntryDto deliveryDocumentEntryDto, BindingResult bindingResult) {
    return deliveryDocumentEntryService.create(deliveryDocumentEntryDto, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<DeliveryDocumentEntryDto> getEntriesFor(@RequestParam(name = "deliveryDocumentNumber") String deliveryDocumentNumber) {
    return deliveryDocumentEntryService.getAllEntriesFor(deliveryDocumentNumber);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DeliveryDocumentEntryDto update(@PathVariable Long id, @RequestBody @Valid DeliveryDocumentEntryDto deliveryDocumentEntryDto, BindingResult bindingResult) {
    return deliveryDocumentEntryService.update(id, deliveryDocumentEntryDto, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    deliveryDocumentEntryService.delete(id);
  }

}

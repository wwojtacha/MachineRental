package machineRental.MR.delivery.document.controller;

import java.time.LocalDate;
import javax.validation.Valid;
import machineRental.MR.delivery.document.model.DeliveryDocumentDto;
import machineRental.MR.delivery.document.service.DeliveryDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/deliveryDocuments")
public class DeliveryDocumentController {

  @Autowired
  private DeliveryDocumentService deliveryDocumentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DeliveryDocumentDto create (@RequestBody @Valid DeliveryDocumentDto deliveryDocumentDto, BindingResult bindingResult) {
    return deliveryDocumentService.create(deliveryDocumentDto, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<DeliveryDocumentDto> search(
      @RequestParam(name = "contractorName", required = false, defaultValue = "") String contractorName,
      @RequestParam(name = "documentNumber", required = false, defaultValue = "") String documentNumber,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(value = "date", required = false) LocalDate date,
      Pageable pageable) {

    return deliveryDocumentService.search(contractorName, documentNumber, date, pageable);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DeliveryDocumentDto update(@PathVariable Long id, @RequestBody @Valid DeliveryDocumentDto deliveryDocument, BindingResult bindingResult) {
    return deliveryDocumentService.update(id, deliveryDocument, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    deliveryDocumentService.delete(id);
  }

  @GetMapping("/{documentNumber}")
  @ResponseStatus(HttpStatus.OK)
  public DeliveryDocumentDto update(@PathVariable String documentNumber) {
    return deliveryDocumentService.getDeliveryDocument(documentNumber);
  }

}

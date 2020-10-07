package machineRental.MR.workDocument.controller;

import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import machineRental.MR.workDocument.DocumentType;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocument.service.WorkDocumentService;
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
@RequestMapping("/workDocuments")
public class WorkDocumentController {

  @Autowired
  private WorkDocumentService workDocumentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public WorkDocument create(@RequestBody @Valid WorkDocument workDocument, BindingResult bindingResult) {
    return workDocumentService.create(workDocument, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<WorkDocument> search(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      @RequestParam(name = "documentType", required = false) List<DocumentType> documentType,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      @RequestParam(name = "date", required = false) LocalDate date,
      @RequestParam(name = "operatorName", required = false, defaultValue = "") String operatorName,
      @RequestParam(name = "machineInternalId", required = false, defaultValue = "") String machineInternalId,
      @RequestParam(name = "delegation", required = false, defaultValue = "") String delegation,
      @RequestParam(name = "invoiceNumber", required = false, defaultValue = "") String invoiceNumber,
      Pageable pageable) {

    return workDocumentService.search(id, documentType, date, operatorName, machineInternalId, delegation, invoiceNumber, pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public WorkDocument getById(@PathVariable String id) {
    return workDocumentService.getById(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public WorkDocument update(@PathVariable String id, @RequestBody @Valid WorkDocument workDocument, BindingResult bindingResult) {
    return workDocumentService.update(id, workDocument, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable String id) {
    workDocumentService.delete(id);
  }

}

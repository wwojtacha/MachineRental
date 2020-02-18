package machineRental.MR.workDocumentEntry.controller;

import java.util.List;
import javax.validation.Valid;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/workReportEntries")
public class WorkReportEntryController {

  @Autowired
  private WorkReportEntryService workReportEntryService;


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<WorkReportEntry> create(@RequestBody @Valid List<WorkReportEntry> workReportEntries, BindingResult bindingResult) {

    return workReportEntryService.create(workReportEntries, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<WorkReportEntry> getEntriesFor(@RequestParam(name = "workDocumentId") String workDocumentId) {
    return workReportEntryService.getEntriesFor(workDocumentId);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    workReportEntryService.delete(id);
  }

}

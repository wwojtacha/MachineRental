package machineRental.MR.workDocumentEntry.controller;

import java.util.List;
import javax.validation.Valid;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
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
@RequestMapping("/roadCardEntries")
public class RoadCardEntryController {

  @Autowired
  private RoadCardEntryService roadCardEntryService;


  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<RoadCardEntry> create(@RequestBody @Valid List<RoadCardEntry> roadCardEntries, BindingResult bindingResult) {

    return roadCardEntryService.create(roadCardEntries, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<RoadCardEntry> getEntriesFor(@RequestParam(name = "workDocumentId") String workDocumentId) {
    return roadCardEntryService.getEntriesFor(workDocumentId);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable Long id) {
    roadCardEntryService.delete(id);
  }

}

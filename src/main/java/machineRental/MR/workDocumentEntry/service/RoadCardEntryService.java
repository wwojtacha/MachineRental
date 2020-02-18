package machineRental.MR.workDocumentEntry.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.repository.RoadCardEntryRepository;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryForValidation;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryValidator;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryHelper;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class RoadCardEntryService {

  private static final long CREATION_MODE_WORK_REPORT_ENTRY_ID = -1;

  @Autowired
  private RoadCardEntryRepository roadCardEntryRepository;

  @Autowired
  private WorkDocumentEntryHelper workDocumentHelper;


  public List<RoadCardEntry> create(List<RoadCardEntry> roadCardEntries, BindingResult bindingResult) {

    Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation = WorkDocumentEntryForValidation.fromRoadCardEntries(roadCardEntries);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = getAllWorkDocumentEntriesForValidation(roadCardEntries, workDocumentEntriesForValidation);

    new WorkDocumentEntryValidator().validateWorkingTime(workDocumentEntriesForValidation, allWorkDocumentEntriesForValidation, bindingResult);



    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      Long id = roadCardEntry.getId();
      if (isToBeCreated(id)) {
        validateRoadCardEntryIdConsistency(CREATION_MODE_WORK_REPORT_ENTRY_ID, CREATION_MODE_WORK_REPORT_ENTRY_ID, bindingResult);
      } else {
        Optional<RoadCardEntry> dbEntry = roadCardEntryRepository.findById(id);

        if (!dbEntry.isPresent()) {
          throw new NotFoundException(String.format("Road card entry with id: \'%s\' does not exist", id));
        }

        Long dbEntryId = dbEntry.get().getId();
        validateRoadCardEntryIdConsistency(id, dbEntryId, bindingResult);
      }

      roadCardEntryRepository.save(roadCardEntry);
    }

    return roadCardEntries;
  }

  private Collection<WorkDocumentEntryForValidation> getAllWorkDocumentEntriesForValidation(List<RoadCardEntry> roadCardEntries, Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation ) {
    Set<RoadCardEntry> dbRoadCardEntries = getDbRoadCardEntries(roadCardEntries);
    Collection<WorkDocumentEntryForValidation> dbRoadCardEntriesForValidation = WorkDocumentEntryForValidation.fromRoadCardEntries(dbRoadCardEntries);

    Collection<WorkReportEntry> dbWorkReportEntriesByOperator = workDocumentHelper.getWorkReportEntriesByOperator(roadCardEntries);
    Collection<WorkDocumentEntryForValidation> dbWorkReportEntriesByOperatorForValidation = WorkDocumentEntryForValidation.fromWorkReportEntries(dbWorkReportEntriesByOperator);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = new ArrayList<>(dbRoadCardEntriesForValidation);
    allWorkDocumentEntriesForValidation.addAll(dbWorkReportEntriesByOperatorForValidation);
    allWorkDocumentEntriesForValidation.addAll(workDocumentEntriesForValidation);

    return allWorkDocumentEntriesForValidation;
  }

  private Set<RoadCardEntry> getDbRoadCardEntries(List<RoadCardEntry> roadCardEntries) {
    List<RoadCardEntry> roadCardEntriesByOperator = workDocumentHelper.getRoadCardEntriesByOperator(roadCardEntries);
    List<RoadCardEntry> rodCardEntriesByMachine = workDocumentHelper.getRoadCardEntriesByMachine(roadCardEntries);

    Set<RoadCardEntry> dbRoadCardEntries = new HashSet<>();
    dbRoadCardEntries.addAll(roadCardEntriesByOperator);
    dbRoadCardEntries.addAll(rodCardEntriesByMachine);

    return dbRoadCardEntries;
  }

  private boolean isToBeCreated(Long id) {
    return id == null;
  }

  public List<RoadCardEntry> getEntriesFor(String workDocumentId) {
    return roadCardEntryRepository.findAllByWorkDocument_Id(workDocumentId);
  }

  private void validateRoadCardEntryIdConsistency(Long id, Long currentId, BindingResult bindingResult) {
    if(roadCardEntryRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "roadCardEntry",
          "id",
          String.format("Road card entry with id: \'%s\' already exists", id)));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void deleteByWorkDocument(String workDocumentId) {

    List<RoadCardEntry> roadCardEntries = roadCardEntryRepository.findAllByWorkDocument_Id(workDocumentId);

    if (roadCardEntries.isEmpty()) {
      return;
    }

    roadCardEntryRepository.deleteByWorkDocument_Id(workDocumentId);
  }

  public void delete(Long id) {

    Optional<RoadCardEntry> dbWorkReportEntry = roadCardEntryRepository.findById(id);

    if (!dbWorkReportEntry.isPresent()) {
      throw new NotFoundException(String.format("Road card entry with id \'%s\' doesn`t exist!", id));
    }

    roadCardEntryRepository.deleteById(id);
  }

}




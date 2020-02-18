package machineRental.MR.workDocumentEntry.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.repository.WorkReportEntryRepository;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryForValidation;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryValidator;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Transactional
@Service
public class WorkReportEntryService {

  private static final long CREATION_MODE_WORK_REPORT_ENTRY_ID = -1;

  @Autowired
  private WorkReportEntryRepository workReportEntryRepository;

  @Autowired
  private WorkDocumentEntryHelper workDocumentHelper;


  public List<WorkReportEntry> create(List<WorkReportEntry> workReportEntries, BindingResult bindingResult) {


    Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation = WorkDocumentEntryForValidation.fromWorkReportEntries(workReportEntries);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = getAllWorkDocumentEntriesForValidation(workReportEntries, workDocumentEntriesForValidation);

    new WorkDocumentEntryValidator().validateWorkingTime(workDocumentEntriesForValidation, allWorkDocumentEntriesForValidation, bindingResult);


    for (WorkReportEntry workReportEntry : workReportEntries) {
      Long id = workReportEntry.getId();
      if (isToBeCreated(id)) {
        validateWorkReportEntryIdConsistency(CREATION_MODE_WORK_REPORT_ENTRY_ID, CREATION_MODE_WORK_REPORT_ENTRY_ID, bindingResult);
      } else {
        Optional<WorkReportEntry> dbEntry = workReportEntryRepository.findById(id);

        if (!dbEntry.isPresent()) {
          throw new NotFoundException(String.format("Work report entry with id: \'%s\' does not exist", id));
        }

        Long dbEntryId = dbEntry.get().getId();
        validateWorkReportEntryIdConsistency(id, dbEntryId, bindingResult);
      }

      workReportEntryRepository.save(workReportEntry);
    }

    return workReportEntries;
  }

  private Collection<WorkDocumentEntryForValidation> getAllWorkDocumentEntriesForValidation(List<WorkReportEntry> workReportEntries, Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation ) {
    Set<WorkReportEntry> dbWorkReportEntries = getDbWorkReportEntries(workReportEntries);
    Collection<WorkDocumentEntryForValidation> dbWorkReportEntriesForValidation = WorkDocumentEntryForValidation.fromWorkReportEntries(dbWorkReportEntries);

    Collection<RoadCardEntry> dbRoadCardEntriesByOperator = workDocumentHelper.getRoadCardEntriesByOperator(workReportEntries);
    Collection<WorkDocumentEntryForValidation> dbRoadCardEntriesByOperatorForValidation = WorkDocumentEntryForValidation.fromRoadCardEntries(dbRoadCardEntriesByOperator);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = new ArrayList<>(dbWorkReportEntriesForValidation);
    allWorkDocumentEntriesForValidation.addAll(dbRoadCardEntriesByOperatorForValidation);
    allWorkDocumentEntriesForValidation.addAll(workDocumentEntriesForValidation);

    return allWorkDocumentEntriesForValidation;
  }

  private Set<WorkReportEntry> getDbWorkReportEntries(List<WorkReportEntry> workReportEntries) {
    List<WorkReportEntry> workReportEntriesByOperator = workDocumentHelper.getWorkReportEntriesByOperator(workReportEntries);
    List<WorkReportEntry> workReportEntriesByMachine = workDocumentHelper.getWorkReportEntriesByMachine(workReportEntries);

    Set<WorkReportEntry> dbWorkReportEntries = new HashSet<>();
    dbWorkReportEntries.addAll(workReportEntriesByOperator);
    dbWorkReportEntries.addAll(workReportEntriesByMachine);

    return dbWorkReportEntries;
  }

  private boolean isToBeCreated(Long id) {
    return id == null;
  }

  public List<WorkReportEntry> getEntriesFor(String workDocumentId) {
    return workReportEntryRepository.findAllByWorkDocument_Id(workDocumentId);
  }

  private void validateWorkReportEntryIdConsistency(Long id, Long currentId, BindingResult bindingResult) {
    if(workReportEntryRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "workReportEntry",
          "id",
          String.format("Work report entry with id: \'%s\' already exists", id)));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void deleteByWorkDocument(String workDocumentId) {

    List<WorkReportEntry> workReportEntries = workReportEntryRepository.findAllByWorkDocument_Id(workDocumentId);

    if (workReportEntries.isEmpty()) {
      return;
    }

    workReportEntryRepository.deleteByWorkDocument_Id(workDocumentId);
  }

  public void delete(Long id) {

    Optional<WorkReportEntry> dbWorkReportEntry = workReportEntryRepository.findById(id);

    if (!dbWorkReportEntry.isPresent()) {
      throw new NotFoundException(String.format("Work report entry with id \'%s\' doesn`t exist!", id));
    }

    workReportEntryRepository.deleteById(id);
  }
}

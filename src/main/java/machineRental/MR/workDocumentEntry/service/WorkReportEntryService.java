package machineRental.MR.workDocumentEntry.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.hour.HourPriceChecker;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.price.hour.service.HourPriceService;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;
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

@Service
public class WorkReportEntryService {

  private static final long CREATION_MODE_WORK_REPORT_ENTRY_ID = -1;

  @Autowired
  private WorkReportEntryRepository workReportEntryRepository;

  @Autowired
  private WorkDocumentEntryHelper workDocumentHelper;

  @Autowired
  private HourPriceService hourPriceService;

  @Autowired
  WorkDocumentEntryValidator workDocumentEntryValidator;

  @Autowired
  private HourPriceChecker hourPriceChecker;


  public List<WorkReportEntry> create(List<WorkReportEntry> workReportEntries, BindingResult bindingResult) {

    checkWorkingTime(workReportEntries, bindingResult);

    for (WorkReportEntry workReportEntry : workReportEntries) {

      workDocumentEntryValidator.validateWorkReportEntryQuantity(workReportEntry, bindingResult);

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

  public void checkWorkingTime(List<WorkReportEntry> workReportEntries, BindingResult bindingResult) {
    Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation = WorkDocumentEntryForValidation.fromWorkReportEntries(workReportEntries);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = getAllWorkDocumentEntriesForValidation(workReportEntries, workDocumentEntriesForValidation);

    workDocumentEntryValidator.validateWorkingTime(workDocumentEntriesForValidation, allWorkDocumentEntriesForValidation, bindingResult);
  }

  private Collection<WorkDocumentEntryForValidation> getAllWorkDocumentEntriesForValidation(List<WorkReportEntry> workReportEntries,
      Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation) {
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
    return workReportEntryRepository.getAllByWorkDocument_Id(workDocumentId);
  }

  private void validateWorkReportEntryIdConsistency(Long id, Long currentId, BindingResult bindingResult) {
    if (workReportEntryRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "workReportEntry",
          "id",
          String.format("Work report entry with id: \'%s\' already exists", id)));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  @Transactional
  public void deleteByWorkDocument(String workDocumentId) {

    List<WorkReportEntry> workReportEntries = workReportEntryRepository.getAllByWorkDocument_Id(workDocumentId);

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

  public void checkPrices(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    String documentNumber = dbWorkDocument.getId();
    LocalDate editedDate = editedWorkDocument.getDate();

    List<WorkReportEntry> workReportEntries = workReportEntryRepository.getAllByWorkDocument_Id(documentNumber);

    for (WorkReportEntry workReportEntry : workReportEntries) {
      String editedMachineNumber = editedWorkDocument.getMachine().getInternalId();
      List<HourPrice> matchingPrices = hourPriceService.getMatchingPrices(editedMachineNumber, editedDate);

      boolean isMatchingPrice = false;

      MATCHING_PRICES:
      for (HourPrice matchingPrice : matchingPrices) {

        HourPrice hourPrice = workReportEntry.getHourPrice();

        if (hourPriceService.isPriceMatching(editedDate, hourPrice, matchingPrice, editedMachineNumber)) {
          workReportEntry.setHourPrice(matchingPrice);
          isMatchingPrice = true;
          break MATCHING_PRICES;
        }
      }

      WorkCode workCode = workReportEntry.getWorkCode();
      PriceType priceType = workReportEntry.getHourPrice().getPriceType();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no hour price matching editedDate %s and price parameters: %s, %s, %s.", editedDate, workCode, editedMachineNumber, priceType));
      }

    }
  }

  public List<WorkReportEntry> getWorkReportEntriesByHourPrice(Long priceId) {
    return workReportEntryRepository.findAllByHourPrice_Id(priceId);
  }

  public void updateOnEstimatePositionChange(Long id, EstimatePosition editedEstimatePosition) {

    List<WorkReportEntry> workReportEntries = getWorkReportEntriesByEstimatePosition_Id(id);

    if (workReportEntries.isEmpty()) {
      return;
    }

    String editedEstimateProjectCode = editedEstimatePosition.getCostCode().getProjectCode();

    List<HourPrice> hourPricesByProjectCode = hourPriceService.getHourPricesByProjectCode(editedEstimateProjectCode);


    for (WorkReportEntry workReportEntry : workReportEntries) {

      boolean isMatchingPrice = false;

      for (HourPrice hourPrice : hourPricesByProjectCode) {
        if (hourPriceChecker.isPriceMatchingEditedEstimateProjectCode(workReportEntry, hourPrice)) {
          workReportEntry.setHourPrice(hourPrice);
          editedEstimatePosition.setId(id);
          workReportEntry.setEstimatePosition(editedEstimatePosition);
//          workReportEntryRepository.save(workReportEntry);
          isMatchingPrice = true;
          break;
        }
      }

      WorkDocument workDocument = workReportEntry.getWorkDocument();
      WorkCode workCode = workReportEntry.getWorkCode();
      String machineNumber = workDocument.getMachine().getInternalId();
      PriceType priceType = workReportEntry.getHourPrice().getPriceType();
      LocalDate date = workDocument.getDate();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no hour price matching edited estimate projct code %s and work report entry parameters: %s, %s, %s, %s.",
            editedEstimateProjectCode,
            workCode,
            machineNumber,
            priceType,
            date));
      }

    }

  }

  public List<WorkReportEntry> getWorkReportEntriesByEstimatePosition_Id(Long estimateId) {
    return workReportEntryRepository.findByEstimatePosition_Id(estimateId);
  }

  public List<WorkReportEntry> getWorkReportEntriesBetweenDatesByEstimateProjectCode(LocalDate startDate, LocalDate endDate, String projectCode) {
    return workReportEntryRepository.findByWorkDocument_DateBetweenAndEstimatePosition_CostCode_ProjectCodeEquals(startDate, endDate, projectCode);
  }

  public List<WorkReportEntry> getWorkReportEntriesBetweenDates(LocalDate startDate, LocalDate endDate) {
    return workReportEntryRepository.findByWorkDocument_DateBetween(startDate, endDate);
  }
}

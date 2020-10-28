package machineRental.MR.workDocumentEntry.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.distance.DistancePriceChecker;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.price.distance.service.DistancePriceService;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;
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

  @Autowired
  private DistancePriceService distancePriceService;

  @Autowired
  private WorkDocumentEntryValidator workDocumentEntryValidator;

  @Autowired
  private DistancePriceChecker distancePriceChecker;


  public List<RoadCardEntry> create(List<RoadCardEntry> roadCardEntries, BindingResult bindingResult) {

    checkWorkingTime(roadCardEntries, bindingResult);

    for (RoadCardEntry roadCardEntry : roadCardEntries) {

      workDocumentEntryValidator.validateRoadCardEntryQuantity(roadCardEntry, bindingResult);
      workDocumentEntryValidator.validateRoadCardEntryDistance(roadCardEntry, bindingResult);

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

  public void checkWorkingTime(List<RoadCardEntry> roadCardEntries, BindingResult bindingResult) {
    Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation = WorkDocumentEntryForValidation.fromRoadCardEntries(roadCardEntries);

    Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation = getAllWorkDocumentEntriesForValidation(roadCardEntries, workDocumentEntriesForValidation);

    workDocumentEntryValidator.validateWorkingTime(workDocumentEntriesForValidation, allWorkDocumentEntriesForValidation, bindingResult);
  }

  private Collection<WorkDocumentEntryForValidation> getAllWorkDocumentEntriesForValidation(List<RoadCardEntry> roadCardEntries,
      Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation) {
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
    if (roadCardEntryRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "roadCardEntry",
          "id",
          String.format("Road card entry with id: \'%s\' already exists", id)));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  @Transactional
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

  public void checkPrices(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    String documentNumber = dbWorkDocument.getId();
    LocalDate editedDate = editedWorkDocument.getDate();

    List<RoadCardEntry> roadCardEntries = roadCardEntryRepository.findAllByWorkDocument_Id(documentNumber);

    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      String editedMachineNumber = editedWorkDocument.getMachine().getInternalId();
      List<DistancePrice> matchingPrices = distancePriceService.getMatchingPrices(editedMachineNumber, editedDate);

      boolean isMatchingPrice = false;

      MATCHING_PRICES:
      for (DistancePrice matchingPrice : matchingPrices) {

        DistancePrice distancePrice = roadCardEntry.getDistancePrice();

        if (distancePriceService.isPriceMatching(editedDate, distancePrice, matchingPrice, editedMachineNumber)) {
          roadCardEntry.setDistancePrice(matchingPrice);
          isMatchingPrice = true;
          break MATCHING_PRICES;
        }
      }

      WorkCode workCode = roadCardEntry.getWorkCode();
      PriceType priceType = roadCardEntry.getDistancePrice().getPriceType();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no distance price matching editedDate %s and price parameters: %s, %s, %s.", editedDate, workCode, editedMachineNumber, priceType));
      }

    }
  }

  public List<RoadCardEntry> getWorkReportEntriesByDistancePrice(Long priceId) {
    return roadCardEntryRepository.findAllByDistancePrice_Id(priceId);
  }

  public void updateOnEstimatePositionChange(Long id, EstimatePosition editedEstimatePosition) {

    List<RoadCardEntry> roadCardEntries = getRoadCardEntriesByEstimatePosition_Id(id);

    if (roadCardEntries.isEmpty()) {
      return;
    }

    String editedEstimateProjectCode = editedEstimatePosition.getCostCode().getProjectCode();

    List<DistancePrice> distancePricesByProjectCode = distancePriceService.getDistancePricesByProjectCode(editedEstimateProjectCode);


    for (RoadCardEntry roadCardEntry : roadCardEntries) {

      boolean isMatchingPrice = false;

      for (DistancePrice distancePrice : distancePricesByProjectCode) {
        if (distancePriceChecker.isPriceMatchingEditedEstimateProjectCode(roadCardEntry, distancePrice)) {
          roadCardEntry.setDistancePrice(distancePrice);
          editedEstimatePosition.setId(id);
          roadCardEntry.setEstimatePosition(editedEstimatePosition);
//          workReportEntryRepository.save(roadCardEntry);
          isMatchingPrice = true;
          break;
        }
      }

      WorkDocument workDocument = roadCardEntry.getWorkDocument();
      WorkCode workCode = roadCardEntry.getWorkCode();
      String machineNumber = workDocument.getMachine().getInternalId();
      PriceType priceType = roadCardEntry.getDistancePrice().getPriceType();
      double distance = roadCardEntry.getDistance();
      LocalDate date = workDocument.getDate();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no distance price matching edited estimate projct code %s and road card entry parameters: %s, %s, %s, %s, %s.",
            editedEstimateProjectCode,
            workCode,
            machineNumber,
            priceType,
            distance,
            date));
      }

    }
  }

  public List<RoadCardEntry> getRoadCardEntriesByEstimatePosition_Id(Long estimateId) {
    return roadCardEntryRepository.findByEstimatePosition_Id(estimateId);
  }

  public List<RoadCardEntry> getRoadCardEntriesByEstimateProjectCode(String projectCode) {
    return roadCardEntryRepository.findByEstimatePosition_CostCode_ProjectCode(projectCode);
  }

  public List<RoadCardEntry> getRoadCardEntriesBetweenDates(LocalDate startDate, LocalDate endDate) {
    return roadCardEntryRepository.findByWorkDocument_DateBetween(startDate, endDate);
  }

  public List<RoadCardEntry> getRoadCardEntriesBetweenDatesByEstimateProjectCode(LocalDate startDate, LocalDate endDate, String projectCode) {
    return roadCardEntryRepository.findByWorkDocument_DateBetweenAndEstimatePosition_CostCode_ProjectCodeEquals(startDate, endDate, projectCode);
  }
}





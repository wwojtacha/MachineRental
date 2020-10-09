package machineRental.MR.price.distance;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.price.PriceChecker;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.price.distance.service.DateChecker;
import machineRental.MR.price.hour.exception.OverlappingDatesException;
import machineRental.MR.repository.DistancePriceRepository;
import machineRental.MR.repository.RoadCardEntryRepository;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;

@Service
public class DistancePriceChecker implements PriceChecker {

  @Autowired
  private RoadCardEntryRepository roadCardEntryRepository;

  @Autowired
  private DistancePriceRepository distancePriceRepository;

  private DateChecker dateChecker = new DateChecker();

  @Override
  public boolean isPriceAlreadyUsed(Long priceId) {
    return roadCardEntryRepository.existsByDistancePrice_Id(priceId);
  }

  public void checkEditability(Long id, DistancePrice currentDistancePrice, DistancePrice editedDistancePrice) {
    List<RoadCardEntry> roadCardEntires = roadCardEntryRepository.findAllByDistancePrice_Id(id);

//    roadCardEntries found by DistancePrice id all concern the same machine
    String machineInternalId = "";
    if (!roadCardEntires.isEmpty()) {
      WorkDocument workDocument = roadCardEntires.iterator().next().getWorkDocument();
      machineInternalId = workDocument.getMachine().getInternalId();
    }
    checkDistancePriceUniquness(currentDistancePrice, editedDistancePrice, machineInternalId);

    checkDistancePriceMatch(editedDistancePrice, roadCardEntires);
//    no exception thrown upto now so edited price can be updated
  }

  private void checkDistancePriceUniquness(DistancePrice currentDistancePrice, DistancePrice editedDistancePrice, String machineInternalId) {
    //    uniqueness of editedDistancePrice needs to be checked ony against existing hour prices for a given machine. All prices for different machines will be unique by definition.
    List<DistancePrice> distancePricesByMachine = distancePriceRepository.findByMachineInternalIdEquals(machineInternalId);

    for (DistancePrice distancePriceByMachine : distancePricesByMachine) {
//      useless to check if current price to be edited is unique against itself
      if (currentDistancePrice == distancePriceByMachine) {
        continue;
      }

      if (!isPriceUnique(editedDistancePrice, distancePriceByMachine)) {
        throw new OverlappingDatesException(
            String.format("Distance price for a given work code (%s), machine number (%s), price type (%s), distance ranges %s - %s cannot overlap in time with the same entry.",
                editedDistancePrice.getWorkCode(),
                editedDistancePrice.getMachine().getInternalId(),
                editedDistancePrice.getPriceType(),
                editedDistancePrice.getRangeMin(),
                editedDistancePrice.getRangeMax()));
      }
    }
  }

  public boolean isPriceUnique(DistancePrice newPrice, DistancePrice price) {
    return newPrice.getWorkCode() != price.getWorkCode()
        || !newPrice.getMachine().getInternalId().equals(price.getMachine().getInternalId())
        || newPrice.getPriceType() != price.getPriceType()
        || !newPrice.getProjectCode().equals(price.getProjectCode())
        || !dateChecker.areDatesOverlapping(newPrice, price)
        || !areDistanceRangesOverlapping(newPrice, price);
  }

  public boolean areDistanceRangesOverlapping(DistancePrice newPrice, DistancePrice price) {
    double newDistancePriceRangeMin = newPrice.getRangeMin();
    double newDistancePriceRangeMax = newPrice.getRangeMax();
    double createdDistancePriceRrangeMin = price.getRangeMin();
    double createdDistancePriceRrangeMax = price.getRangeMin();

    return newDistancePriceRangeMin >= createdDistancePriceRrangeMin && newDistancePriceRangeMin <= createdDistancePriceRrangeMax
        || newDistancePriceRangeMax <= createdDistancePriceRrangeMax && newDistancePriceRangeMax >= createdDistancePriceRrangeMin;

  }

  private void checkDistancePriceMatch(DistancePrice editedDistancePrice, List<RoadCardEntry> roadCardEntires) {
    for (RoadCardEntry roadCardEntry : roadCardEntires) {

      if (!isPriceMatching(roadCardEntry, editedDistancePrice)) {
        WorkDocument workDocument = roadCardEntry.getWorkDocument();
        String workDocumentNumber = workDocument.getId();
        WorkCode workCode = roadCardEntry.getWorkCode();
        PriceType priceType = roadCardEntry.getDistancePrice().getPriceType();
        double distance = roadCardEntry.getDistance();
        String estimateProjectCode = roadCardEntry.getEstimatePosition().getCostCode().getProjectCode();
        LocalDate date = workDocument.getDate();

        throw new NotFoundException(String.format("Edited distance price does not match work document %s entry parameters: %s, %s, %s, %s, %s.",
            workDocumentNumber,
            workCode,
            priceType,
            distance,
            estimateProjectCode,
            date));
      }
    }
  }

  private boolean isPriceMatching(RoadCardEntry roadCardEntry, DistancePrice editedDistancePrice) {

    WorkDocument workDocument = roadCardEntry.getWorkDocument();

    LocalDate date = workDocument.getDate();
    String machineInternalId = workDocument.getMachine().getInternalId();

    return roadCardEntry.getWorkCode() == editedDistancePrice.getWorkCode()
        && machineInternalId.equals(editedDistancePrice.getMachine().getInternalId())
        && roadCardEntry.getDistancePrice().getPriceType() == editedDistancePrice.getPriceType()
        && roadCardEntry.getEstimatePosition().getCostCode().getProjectCode().equals(editedDistancePrice.getProjectCode())
        && isDistanceMatching(roadCardEntry.getDistance(), editedDistancePrice)
        && (date.isAfter(editedDistancePrice.getStartDate()) || date.isEqual(editedDistancePrice.getStartDate()))
        && (date.isBefore(editedDistancePrice.getEndDate()) || date.isEqual(editedDistancePrice.getEndDate()));
  }

  public boolean isDistanceMatching(double distance, DistancePrice price) {
    double rangeMin = price.getRangeMin();
    double rangeMax = price.getRangeMax();

    return distance >= rangeMin && distance <= rangeMax;
  }








  public boolean areSameDistanceRanges(DistancePrice editedDistancePrice, DistancePrice dbPrice) {
    return ((Double) dbPrice.getRangeMin()).equals((Double) editedDistancePrice.getRangeMin()) && ((Double) dbPrice.getRangeMax()).equals((Double) editedDistancePrice.getRangeMax());
  }

  /**
   * Check of distance price project code against road card entry project code is not done as they will not be the same, beacuse method is intended for checking
   * if road card entry can be matched to another price after changing estimate position project code, which is a key for mathcing price and estimate position.
   * @param roadCardEntry checked work report entry
   * @param matchingDistancePrice price against whcih work report entry is checked
   * @return
   */
  public boolean isPriceMatchingEditedEstimateProjectCode(RoadCardEntry roadCardEntry, DistancePrice matchingDistancePrice) {

    WorkDocument workDocument = roadCardEntry.getWorkDocument();
    String machineInternalId = workDocument.getMachine().getInternalId();
    LocalDate date = workDocument.getDate();

    return roadCardEntry.getWorkCode() == matchingDistancePrice.getWorkCode()
        && machineInternalId.equals(matchingDistancePrice.getMachine().getInternalId())
        && roadCardEntry.getDistancePrice().getPriceType() == matchingDistancePrice.getPriceType()
//        && roadCardEntry.getEstimatePosition().getCostCode().getProjectCode().equals(matchingDistancePrice.getProjectCode())
        && isDistanceMatching(roadCardEntry.getDistance(), matchingDistancePrice)
        && (date.isAfter(matchingDistancePrice.getStartDate()) || date.isEqual(matchingDistancePrice.getStartDate()))
        && (date.isBefore(matchingDistancePrice.getEndDate()) || date.isEqual(matchingDistancePrice.getEndDate()));
  }

}

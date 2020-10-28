package machineRental.MR.reports.cost.transport;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportCostCalculator {

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents TotalTransportCost for a specific estimate position.
   */
  public Map<EstimatePosition, TotalTransportCost> getTotalTransportCostByEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {

    MultiKeyMap<Object, TransportCost> transportCostsMultiKeyMap = getTransportCostForEstimatePositionAndMachineType(startDate, endDate, projectCode);

    Map<EstimatePosition, TotalTransportCost> totalTransportCostsMap = getTotalTransportCostForEstimatePosition(transportCostsMultiKeyMap);

    return totalTransportCostsMap;
  }

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents TransportCost for a given estimate position and machine type. Transport cost consists of machine type, sum of work hours for that
   * machine type and sum of cost value for that machine type.
   */
  private MultiKeyMap<Object, TransportCost> getTransportCostForEstimatePositionAndMachineType(LocalDate startDate, LocalDate endDate, String projectCode) {
    List<RoadCardEntry> roadCardEntries = roadCardEntryService.getRoadCardEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);

    MultiKeyMap<Object, TransportCost> transportCostsMultiKeyMap = new MultiKeyMap<>();

    for (RoadCardEntry roadCardEntry : roadCardEntries) {

      EstimatePosition estimatePosition = roadCardEntry.getEstimatePosition();
      MachineType machineType = roadCardEntry.getWorkDocument().getMachine().getMachineType();
      double currentHoursCount = (double) Duration.between(roadCardEntry.getStartHour(), roadCardEntry.getEndHour()).toSeconds() / 3600;
      BigDecimal currentTransportCost = BigDecimal.valueOf(currentHoursCount).multiply(roadCardEntry.getDistancePrice().getPrice());

      TransportCost transportCost = transportCostsMultiKeyMap.get(estimatePosition, machineType);

      if (transportCost == null) {
        transportCost = new TransportCost();
        transportCost.setMachineType(machineType);
        transportCost.setWorkHoursCount(currentHoursCount);
        transportCost.setCostValue(currentTransportCost);

        transportCostsMultiKeyMap.put(estimatePosition, machineType, transportCost);
      } else {
        double previousWorkHoursCount = transportCost.getWorkHoursCount();
        BigDecimal previousCostValue = transportCost.getCostValue();

        transportCost.setWorkHoursCount(previousWorkHoursCount + currentHoursCount);
        transportCost.setCostValue(previousCostValue.add(currentTransportCost));

        transportCostsMultiKeyMap.put(estimatePosition, machineType, transportCost);
      }

    }
    return transportCostsMultiKeyMap;
  }

  /**
   * @param transportCostsMultiKeyMap Map in which every entry represents TransportCost for a given estimate position and machine type.
   * @return Map in which every entry represents TotaTransportCost for a given estimate position. TotalTransportCost consists of a list of summed up transport costs
   * for each machine type, total work hours count and total cost value for all machine types concerning specific estimate position
   * (that is machine types from TransportCost list).
   */
  private Map<EstimatePosition, TotalTransportCost> getTotalTransportCostForEstimatePosition(MultiKeyMap<Object, TransportCost> transportCostsMultiKeyMap) {
    Map<EstimatePosition, TotalTransportCost> totalTransportCostsMap = new HashMap<>();

    transportCostsMultiKeyMap.forEach((key, value) -> {
      EstimatePosition estimatePosition = (EstimatePosition) key.getKey(0);
      TotalTransportCost totalTransportCost = totalTransportCostsMap.get(estimatePosition);

      double currentWorkHoursCount = value.getWorkHoursCount();
      BigDecimal currentCostValue = value.getCostValue();

      if (totalTransportCost == null) {
        totalTransportCost = new TotalTransportCost();
        List<TransportCost> transportCosts = new ArrayList<>();
        transportCosts.add(value);
        totalTransportCost.setTransportCosts(transportCosts);
        totalTransportCost.setTotalWorkHoursCount(currentWorkHoursCount);
        totalTransportCost.setTotalCostValue(currentCostValue);
        totalTransportCostsMap.put(estimatePosition, totalTransportCost);
      } else {
        List<TransportCost> transportCosts = totalTransportCost.getTransportCosts();
        transportCosts.add(value);

        double previousTotalWorkHoursCount = totalTransportCost.getTotalWorkHoursCount();
        BigDecimal previousTotalCostValue = totalTransportCost.getTotalCostValue();

        totalTransportCost.setTotalWorkHoursCount(previousTotalWorkHoursCount + currentWorkHoursCount);
        totalTransportCost.setTotalCostValue(previousTotalCostValue.add(currentCostValue));

        totalTransportCostsMap.put(estimatePosition, totalTransportCost);
      }
    });
    return totalTransportCostsMap;
  }

}

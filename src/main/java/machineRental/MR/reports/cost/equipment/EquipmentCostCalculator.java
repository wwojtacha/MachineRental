package machineRental.MR.reports.cost.equipment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryValidator;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentCostCalculator {

  @Autowired
  private WorkReportEntryService workReportEntryService;

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents TotalEquipmentCost for a specific estimate position.
   */
  public Map<EstimatePosition, TotalEquipmentCost> getTotalEquipmentCostByEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {

    MultiKeyMap<Object, EquipmentCost> equipmentCostsMultiKeyMap = getEquipmentCostForEstimatePositionAndMachineType(startDate, endDate, projectCode);

    Map<EstimatePosition, TotalEquipmentCost> totalEquipmentCostsMap = getTotalEquipmentCostForEstimatePosition(equipmentCostsMultiKeyMap);

    return totalEquipmentCostsMap;
  }

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents EquipmentCost for a given estimate position and machine type. Equipment cost consists of machine type, sum of work hours for that
   * machine type and sum of cost value for that machine type.
   */
  private MultiKeyMap<Object, EquipmentCost> getEquipmentCostForEstimatePositionAndMachineType(LocalDate startDate, LocalDate endDate, String projectCode) {
    List<WorkReportEntry> workReportEntries = workReportEntryService.getWorkReportEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);

    MultiKeyMap<Object, EquipmentCost> equipmentCostsMultiKeyMap = new MultiKeyMap<>();

    for (WorkReportEntry workReportEntry : workReportEntries) {

      WorkCode workCode = workReportEntry.getWorkCode();

      //      do not calculate neither cost nor hour count of PR activity. This activity is calculated as TotalLabourCost
      if (WorkCode.PR == workCode) {
        continue;
      }

      EstimatePosition estimatePosition = workReportEntry.getEstimatePosition();
      MachineType machineType = workReportEntry.getWorkDocument().getMachine().getMachineType();

      double currentHoursCount = (double) Duration.between(workReportEntry.getStartHour(), workReportEntry.getEndHour()).toSeconds() / 3600;
      BigDecimal currentEquipmentCost = BigDecimal.valueOf(currentHoursCount).multiply(workReportEntry.getHourPrice().getPrice());

      EquipmentCost equipmentCost = equipmentCostsMultiKeyMap.get(estimatePosition, machineType);

      if (!WorkDocumentEntryValidator.EXPLOITATION_WORK_CODES.contains(workReportEntry.getWorkCode())) {
        currentHoursCount = 0;
      }

      if (equipmentCost == null) {
        equipmentCost = new EquipmentCost();
        equipmentCost.setMachineType(machineType);
        equipmentCost.setWorkHoursCount(currentHoursCount);
        equipmentCost.setCostValue(currentEquipmentCost);

        equipmentCostsMultiKeyMap.put(estimatePosition, machineType, equipmentCost);
      } else {
        double previousWorkHoursCount = equipmentCost.getWorkHoursCount();
        BigDecimal previousCostValue = equipmentCost.getCostValue();

        equipmentCost.setWorkHoursCount(previousWorkHoursCount + currentHoursCount);
        equipmentCost.setCostValue(previousCostValue.add(currentEquipmentCost));

        equipmentCostsMultiKeyMap.put(estimatePosition, machineType, equipmentCost);
      }

    }
    return equipmentCostsMultiKeyMap;
  }

  /**
   * @param equipmentCostsMultiKeyMap Map in which every entry represents EquipmentCost for a given estimate position and machine type.
   * @return Map in which every entry represents TotaEquipmentCost for a given estimate position. TotalEquipmentCost consists of a list of summed up equipment costs
   * for each machine type, total work hours count and total cost value for all machine types concerning specific estimate position
   * (that is machine types from EquipmentCost list).
   */
  private Map<EstimatePosition, TotalEquipmentCost> getTotalEquipmentCostForEstimatePosition(MultiKeyMap<Object, EquipmentCost> equipmentCostsMultiKeyMap) {
    Map<EstimatePosition, TotalEquipmentCost> totalEquipmentCostsMap = new HashMap<>();

    equipmentCostsMultiKeyMap.forEach((key, value) -> {
      EstimatePosition estimatePosition = (EstimatePosition) key.getKey(0);
      TotalEquipmentCost totalEquipmentCost = totalEquipmentCostsMap.get(estimatePosition);

      double currentWorkHoursCount = value.getWorkHoursCount();
      BigDecimal currentCostValue = value.getCostValue();

      if (totalEquipmentCost == null) {
        totalEquipmentCost = new TotalEquipmentCost();
        List<EquipmentCost> equipmentCosts = new ArrayList<>();
        equipmentCosts.add(value);
        totalEquipmentCost.setEquipmentCosts(equipmentCosts);
        totalEquipmentCost.setTotalWorkHoursCount(currentWorkHoursCount);
        totalEquipmentCost.setTotalCostValue(currentCostValue);
        totalEquipmentCostsMap.put(estimatePosition, totalEquipmentCost);
      } else {
        List<EquipmentCost> equipmentCosts = totalEquipmentCost.getEquipmentCosts();
        equipmentCosts.add(value);

        double previousTotalWorkHoursCount = totalEquipmentCost.getTotalWorkHoursCount();
        BigDecimal previousTotalCostValue = totalEquipmentCost.getTotalCostValue();

        totalEquipmentCost.setTotalWorkHoursCount(previousTotalWorkHoursCount + currentWorkHoursCount);
        totalEquipmentCost.setTotalCostValue(previousTotalCostValue.add(currentCostValue));

        totalEquipmentCostsMap.put(estimatePosition, totalEquipmentCost);
      }
    });
    return totalEquipmentCostsMap;
  }

}

package machineRental.MR.reports.cost.labour;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.reports.HoursCalculator;
import machineRental.MR.reports.cost.equipment.EquipmentCost;
import machineRental.MR.reports.cost.equipment.TotalEquipmentCost;
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryValidator;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.model.WorkDocumentEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabourCostCalculator {

  @Autowired
  private WorkReportEntryService workReportEntryService;

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  private HoursCalculator hoursCalculator = new HoursCalculator();

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents TotalLabourCost for a given estimate position. TotalLabourCost consists of sum of work hours for that
   * and sum of cost value. Only activities marked as PR are taken into account from work report entries and road card entries.
   */
  public Map<EstimatePosition, TotalLabourCost> getTotalLabourCostForEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {

    Map<EstimatePosition, TotalLabourCost> totalLabourCostsMap = new HashMap<>();

    List<WorkReportEntry> workReportEntries = workReportEntryService.getWorkReportEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);
    totalLabourCostsMap = getEstimatePositionTotalLabourCostMap(totalLabourCostsMap, workReportEntries);

    List<RoadCardEntry> roadCardEntries = roadCardEntryService.getRoadCardEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);
    totalLabourCostsMap = getEstimatePositionTotalLabourCostMap(totalLabourCostsMap, roadCardEntries);


    return totalLabourCostsMap;
  }

  private Map<EstimatePosition, TotalLabourCost> getEstimatePositionTotalLabourCostMap(Map<EstimatePosition, TotalLabourCost> totalLabourCostsMap, List<? extends WorkDocumentEntry> workDocumentEntries) {
    for (WorkDocumentEntry workDocumentEntry : workDocumentEntries) {

      if (WorkCode.PR != workDocumentEntry.getWorkCode()) {
        continue;
      }

      EstimatePosition estimatePosition = workDocumentEntry.getEstimatePosition();

      double currentHoursCount = hoursCalculator.getNumberOfHours(workDocumentEntry);
      BigDecimal currentCostValue = getCostValue(currentHoursCount, workDocumentEntry);

      TotalLabourCost totalLabourCost = totalLabourCostsMap.get(estimatePosition);

      if (totalLabourCost == null) {
        totalLabourCost = new TotalLabourCost();
        totalLabourCost.setTotalWorkHoursCount(currentHoursCount);
        totalLabourCost.setTotalCostValue(currentCostValue);
        totalLabourCostsMap.put(estimatePosition, totalLabourCost);
      } else {

        double previousTotalWorkHoursCount = totalLabourCost.getTotalWorkHoursCount();
        BigDecimal previousTotalCostValue = totalLabourCost.getTotalCostValue();

        totalLabourCost.setTotalWorkHoursCount(previousTotalWorkHoursCount + currentHoursCount);
        totalLabourCost.setTotalCostValue(previousTotalCostValue.add(currentCostValue));

        totalLabourCostsMap.put(estimatePosition, totalLabourCost);
      }
    }

    return totalLabourCostsMap;
  }

  private BigDecimal getCostValue(double hoursCount, WorkDocumentEntry workDocumentEntry) {

    BigDecimal result = BigDecimal.valueOf(0);

    if (workDocumentEntry instanceof WorkReportEntry) {
      result = BigDecimal.valueOf(hoursCount).multiply(((WorkReportEntry) workDocumentEntry).getHourPrice().getPrice());
    } else if (workDocumentEntry instanceof RoadCardEntry) {
      result = BigDecimal.valueOf(hoursCount).multiply(((RoadCardEntry) workDocumentEntry).getDistancePrice().getPrice());
    }

    return result;
  }

}

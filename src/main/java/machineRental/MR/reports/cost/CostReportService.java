package machineRental.MR.reports.cost;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import machineRental.MR.dailyReport.DailyReportService;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.estimate.service.EstimatePositionService;
import machineRental.MR.reports.cost.delivery.DeliveryCostCalculator;
import machineRental.MR.reports.cost.equipment.EquipmentCostCalculator;
import machineRental.MR.reports.cost.equipment.TotalEquipmentCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CostReportService {

  @Autowired
  private EquipmentCostCalculator equipmentCostCalculator;

  @Autowired
  private EstimatePositionService estimatePositionService;

  @Autowired
  private DailyReportService dailyReportService;

  @Autowired
  private DeliveryCostCalculator deliveryCostCalculator;


  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return A list of cost reports. Single cost report is a result of combining total daily report quantity with total equipment cost for a specifin estimate position.
   * Mapping is done by estimate position. If total daily report quantity equals 0 it means that there is no daily report data for given estimate position.
   * If total work hours count equals 0 it means there is no work report entry data for a given estimate position.
   */
  public List<CostReport> getCostReports(LocalDate startDate, LocalDate endDate, String projectCode) {

    List<CostReport> costReports = new ArrayList<>();

    Map<EstimatePosition, TotalEquipmentCost> estimatePositionTotalEquipmentCostMap = equipmentCostCalculator.getTotalEquipmentCostByEstimatePosition(startDate, endDate, projectCode);

    Map<EstimatePosition, BigDecimal> estimatePositionTotalDeliveryCostMap = deliveryCostCalculator.getTotalDeliveryCostPerEstimatePosition(startDate, endDate, projectCode);

    Map<EstimatePosition, Double> estimatePositionToTotalDailyReportQuantity = dailyReportService.getDailyReportQuantityPerEstimatePosition(startDate, endDate, projectCode);

    List<EstimatePosition> estimatePositions = estimatePositionService.getEstimatePositionsByProjectCode(projectCode);

    for (EstimatePosition estimatePosition : estimatePositions) {

      CostReport costReport = new CostReport();
      costReport.setEstimatePosition(estimatePosition);

      Double totalDailyReportQuantity = estimatePositionToTotalDailyReportQuantity.get(estimatePosition);
      if (totalDailyReportQuantity == null) {
        totalDailyReportQuantity = 0.0;
      }
      costReport.setTotalDailyReportQuantity(totalDailyReportQuantity);

      TotalEquipmentCost totalEquipmentCost = estimatePositionTotalEquipmentCostMap.get(estimatePosition);
      if (totalEquipmentCost == null) {
        totalEquipmentCost = new TotalEquipmentCost();
        totalEquipmentCost.setEquipmentCosts(new ArrayList<>());
        totalEquipmentCost.setTotalWorkHoursCount(0);
        totalEquipmentCost.setTotalCostValue(BigDecimal.valueOf(0));

        costReport.setTotalEquipmentCost(totalEquipmentCost);
      } else {
        costReport.setTotalEquipmentCost(totalEquipmentCost);
      }

      BigDecimal totalDeliveryCost = estimatePositionTotalDeliveryCostMap.get(estimatePosition);
      if (totalDeliveryCost == null) {
        totalDeliveryCost = BigDecimal.valueOf(0);
        costReport.setTotalDeliveryCost(totalDeliveryCost);
      } else {
        costReport.setTotalDeliveryCost(totalDeliveryCost);
      }

      costReports.add(costReport);
    }

    return costReports;
  }
}

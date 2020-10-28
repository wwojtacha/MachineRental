package machineRental.MR.reports.cost.delivery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import machineRental.MR.estimate.model.EstimatePosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCostCalculator {

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

  public Map<EstimatePosition, BigDecimal> getTotalDeliveryCostPerEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {
    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryService
        .getDeliveryDocumentEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);


    Map<EstimatePosition, BigDecimal> totalDeliveryCostMap = new HashMap<>();

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {

      EstimatePosition estimatePosition = deliveryDocumentEntry.getEstimatePosition();

      BigDecimal currenntPrice = deliveryDocumentEntry.getDeliveryPrice().getPrice();
      double currentQuantity = deliveryDocumentEntry.getQuantity();
      BigDecimal currentCostValue = currenntPrice.multiply(BigDecimal.valueOf(currentQuantity));

      totalDeliveryCostMap.merge(estimatePosition, currentCostValue, BigDecimal::add);
    }

    return totalDeliveryCostMap;
  }

}

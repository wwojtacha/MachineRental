package machineRental.MR.reports.cost.delivery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.delivery.model.DeliveryPrice;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryCostCalculator {

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

//  public Map<EstimatePosition, BigDecimal> getTotalDeliveryCostPerEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {
//    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryService
//        .getDeliveryDocumentEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);
//
//
//    Map<EstimatePosition, BigDecimal> totalDeliveryCostMap = new HashMap<>();
//
//    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {
//
//      EstimatePosition estimatePosition = deliveryDocumentEntry.getEstimatePosition();
//
//      BigDecimal currenntPrice = deliveryDocumentEntry.getDeliveryPrice().getPrice();
//      double currentQuantity = deliveryDocumentEntry.getQuantity();
//      BigDecimal currentCostValue = currenntPrice.multiply(BigDecimal.valueOf(currentQuantity));
//
//      totalDeliveryCostMap.merge(estimatePosition, currentCostValue, BigDecimal::add);
//    }
//
//    return totalDeliveryCostMap;
//  }

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents TotalDeliveryCost for a specific estimate position.
   */
  public Map<EstimatePosition, TotalDeliveryCost> getTotalDeliveryCostByEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {

    MultiKeyMap<Object, DeliveryCost> deliveryCostsMultiKeyMap = getDeliveryCostForEstimatePositionAndMaterial(startDate, endDate, projectCode);

    Map<EstimatePosition, TotalDeliveryCost> totalDeliveryCostsMap = getTotalDeliveryCostForEstimatePosition(deliveryCostsMultiKeyMap);

    return totalDeliveryCostsMap;
  }

  /**
   * @param startDate Date after which data should be found.
   * @param endDate Date before which data shoud be found.
   * @param projectCode Project code for which data should be found.
   * @return Map in which every entry represents DeliveryCost for a given estimate position and material. Delivery cost consists of material, price type, quantity
   * and sum of cost value for that material.
   */
  private MultiKeyMap<Object, DeliveryCost> getDeliveryCostForEstimatePositionAndMaterial(LocalDate startDate, LocalDate endDate, String projectCode) {
    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryService
        .getDeliveryDocumentEntriesBetweenDatesByEstimateProjectCode(startDate, endDate, projectCode);

    MultiKeyMap<Object, DeliveryCost> deliveryCostsMultiKeyMap = new MultiKeyMap<>();

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {

      EstimatePosition estimatePosition = deliveryDocumentEntry.getEstimatePosition();
      Material material = deliveryDocumentEntry.getMaterial();
      DeliveryPrice deliveryPrice = deliveryDocumentEntry.getDeliveryPrice();
      PriceType priceType = deliveryPrice.getPriceType();

      BigDecimal currentPrice = deliveryPrice.getPrice();
      double currentQuantity = deliveryDocumentEntry.getQuantity();

      BigDecimal currentDeliveryCost = currentPrice.multiply(BigDecimal.valueOf(currentQuantity));

      DeliveryCost deliveryCost = deliveryCostsMultiKeyMap.get(estimatePosition, material, priceType);

      if (deliveryCost == null) {
        deliveryCost = new DeliveryCost();
        deliveryCost.setMaterial(material);
        deliveryCost.setPriceType(priceType);
        deliveryCost.setQuantityCount(currentQuantity);
        deliveryCost.setCostValue(currentDeliveryCost);

        deliveryCostsMultiKeyMap.put(estimatePosition, material, priceType, deliveryCost);
      } else {
        double previousQuantityCount = deliveryCost.getQuantityCount();
        BigDecimal previousCostValue = deliveryCost.getCostValue();

        deliveryCost.setQuantityCount(previousQuantityCount + currentQuantity);
        deliveryCost.setCostValue(previousCostValue.add(currentDeliveryCost));

        deliveryCostsMultiKeyMap.put(estimatePosition, material, priceType, deliveryCost);
      }

    }
    return deliveryCostsMultiKeyMap;
  }

  /**
   * @param deliveryCostsMultiKeyMap Map in which every entry represents DeliveryCost for a given estimate position and material.
   * @return Map in which every entry represents TotaDeliveryCost for a given estimate position. TotalDeliveryCost consists of a list of summed up delivery costs
   * for each material and total cost value for all materials concerning specific estimate position
   * (that is material from DeliveryCost list).
   */
  private Map<EstimatePosition, TotalDeliveryCost> getTotalDeliveryCostForEstimatePosition(MultiKeyMap<Object, DeliveryCost> deliveryCostsMultiKeyMap) {
    Map<EstimatePosition, TotalDeliveryCost> totalDeliveryCostsMap = new HashMap<>();

    deliveryCostsMultiKeyMap.forEach((key, value) -> {
      EstimatePosition estimatePosition = (EstimatePosition) key.getKey(0);
      TotalDeliveryCost totalDeliveryCost = totalDeliveryCostsMap.get(estimatePosition);

      BigDecimal currentCostValue = value.getCostValue();

      if (totalDeliveryCost == null) {
        totalDeliveryCost = new TotalDeliveryCost();
        List<DeliveryCost> deliveryCosts = new ArrayList<>();
        deliveryCosts.add(value);
        totalDeliveryCost.setDeliveryCosts(deliveryCosts);
        totalDeliveryCost.setTotalCostValue(currentCostValue);
        totalDeliveryCostsMap.put(estimatePosition, totalDeliveryCost);
      } else {
        List<DeliveryCost> deliveryCosts = totalDeliveryCost.getDeliveryCosts();
        deliveryCosts.add(value);

        BigDecimal previousTotalCostValue = totalDeliveryCost.getTotalCostValue();

        totalDeliveryCost.setTotalCostValue(previousTotalCostValue.add(currentCostValue));

        totalDeliveryCostsMap.put(estimatePosition, totalDeliveryCost);
      }
    });
    return totalDeliveryCostsMap;
  }
}

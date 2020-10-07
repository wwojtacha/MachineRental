package machineRental.MR.delivery.entry.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import machineRental.MR.client.model.Client;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.delivery.model.DeliveryPrice;

@Data
@AllArgsConstructor
public class DeliveryDocumentEntryDto {

  private Long id;

  private Client contractor;

  private Material material;

  private double quantity;

  private String measureUnit;

  private EstimatePosition estimatePosition;

  private CostCode costCode;

  private DeliveryPrice deliveryPrice;

  private BigDecimal costValue;

  private String invoiceNumber;

  private DeliveryDocument deliveryDocument;

  public DeliveryDocumentEntryDto(final Long id, final Client contractor, final Material material, final double quantity, final String measureUnit, final EstimatePosition estimatePosition,
      final CostCode costCode, final DeliveryPrice deliveryPrice, final String invoiceNumber, final DeliveryDocument deliveryDocument) {
    this.id = id;
    this.contractor = contractor;
    this.material = material;
    this.quantity = quantity;
    this.measureUnit = measureUnit;
    this.estimatePosition = estimatePosition;
    this.costCode = costCode;
    this.deliveryPrice = deliveryPrice;
    this.invoiceNumber = invoiceNumber;
    this.deliveryDocument = deliveryDocument;
  }
}

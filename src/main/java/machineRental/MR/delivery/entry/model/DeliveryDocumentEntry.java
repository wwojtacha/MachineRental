package machineRental.MR.delivery.entry.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.client.model.Client;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.delivery.model.DeliveryPrice;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_documents_entries")
public class DeliveryDocumentEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "contractor_id")
  private Client contractor;

  @ManyToOne
  @JoinColumn(name = "material_id")
  private Material material;

  @NotNull
  @Column
  private double quantity;

  @NotNull
  @Column
  private String measureUnit;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "estimatePosition_id")
  private EstimatePosition estimatePosition;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "code_id")
  private CostCode costCode;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "deliveryPrice_id")
  private DeliveryPrice deliveryPrice;

  @NotNull
  @Column
  private String invoiceNumber = "NOT DEFINED";

  @NotNull
  @ManyToOne
  @JoinColumn(name = "deliveryDocument_id")
  private DeliveryDocument deliveryDocument;
}

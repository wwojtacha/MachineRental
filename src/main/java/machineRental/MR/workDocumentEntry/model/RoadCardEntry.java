package machineRental.MR.workDocumentEntry.model;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.material.model.Material;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;

@Data
@Entity
@Table(name = "road_cards_entries")
public class RoadCardEntry extends WorkDocumentEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Column
  private WorkCode workCode;

  @NotNull
  @Column
  private LocalTime startHour;

  @NotNull
  @Column
  private LocalTime endHour;

  @NotNull
  @Column
  private String loadingPlace;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "material_id")
  private Material material;

  @NotNull
  @Column
  private String unloadingPlace;

  @NotNull
  @Column
  private double quantity;

  @NotEmpty
  @Column
  private String measureUnit;

  @NotNull
  @Column
  private double distance;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "distancePrice_id")
  private DistancePrice distancePrice;

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
  @JoinColumn(name = "operator_id")
  private Operator acceptingPerson;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "workDocument_id")
  private WorkDocument workDocument;

}

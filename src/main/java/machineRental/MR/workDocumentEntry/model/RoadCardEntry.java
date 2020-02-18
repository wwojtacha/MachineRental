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
import javax.validation.constraints.NotNull;
import lombok.Data;
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
  @Column
  private String materialType;

  @NotNull
  @Column
  private String unloadingPlace;

  @NotNull
  @Column
  private int quantity;

  @NotNull
  @Column
  private int distance;

  @NotNull
  @Column
  private String mpk;

  @NotNull
  @Column
  private String acceptingPerson;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "workDocument_id")
  private WorkDocument workDocument;

}

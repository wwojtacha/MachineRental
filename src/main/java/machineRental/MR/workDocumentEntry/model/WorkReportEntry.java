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
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocument.model.WorkDocument;

@Data
@Entity
@Table(name = "work_reports_entries")
public class WorkReportEntry extends WorkDocumentEntry {

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
  private String placeOfWork;

  @NotNull
  @Column
  private String typeOfWork;

  @NotNull
  @Column
  private int workQuantity;

  @NotNull
  @Column
  private String measureUnit;

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

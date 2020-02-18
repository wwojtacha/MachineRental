package machineRental.MR.workDocument.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import machineRental.MR.workDocument.DocumentType;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;

@Data
@Entity
@Table(name = "work_documents")
public class WorkDocument {

  @Id
  private String id;

  @NotNull
  @Column
  @Enumerated(EnumType.STRING)
  private DocumentType documentType;

  @NotNull
  @Column
  private LocalDate date;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "operator_id")
  private Operator operator;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "machine_id")
  private Machine machine;

  @NotNull
  @Column
  private int counterStart;

  @NotNull
  @Column
  private int counterEnd;

}

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
import javax.validation.constraints.Pattern;
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
  private double counterStart;

  @NotNull
  @Column
  private double counterEnd;

  @NotNull
  @Column
  @Pattern(regexp = "0%|50%|100%")
  private String delegation;

  @NotNull
  @Column
  private String invoiceNumber = "NOT DEFINED";

}

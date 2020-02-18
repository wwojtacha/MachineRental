package machineRental.MR.operator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import machineRental.MR.client.model.Client;

@Entity
@Data
@Table(name = "operators")
public class Operator {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank(message = "Operator must have a name.")
  @NotNull
  @Column(nullable = false)
  private String name;

  @Column
  private String qualifications = "";

  @ManyToOne
  @NotNull
  @JoinColumn(name = "client_id")
  private Client company;
}

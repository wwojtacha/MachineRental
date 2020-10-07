package machineRental.MR.costcode.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Data
@Table(name = "codes")
public class CostCode {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotEmpty(message = "Project code is required.")
  @Column(nullable = false)
  private String projectCode;

  @NotEmpty(message = "Type of cost is required.")
  @Column(nullable = false)
  private String costType;

  @NotEmpty(message = "Project code description is required.")
  @Column(nullable = false)
  private String projectCodeDescription;

  @NotEmpty(message = "Type of cost description is required.")
  @Column(nullable = false)
  private String costTypeDescription;

  @NotEmpty
  @Column(nullable = false, unique = true)
  private String fullCode = "default";
}

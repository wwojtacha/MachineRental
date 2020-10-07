package machineRental.MR.estimate.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import machineRental.MR.costcode.model.CostCode;

@Entity
@Data
@Table(name = "estimates")
public class EstimatePosition {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotEmpty(message = "Name of estimate position is required.")
  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "code_id")
  private CostCode costCode;

  @NotNull(message = "Quantity of estimate position is required.")
  @Column(nullable = false)
  private double quantity;

  @NotEmpty(message = "Measure unit of estimate position is required.")
  @Column(nullable = false)
  private String measureUnit;

  @NotNull(message = "Sell price of estimate position is required.")
  @Min(value = 0, message = "Price cannot be lower than 0.")
  @Column(nullable = false)
  private BigDecimal sellPrice;

  @NotNull()
  @Column(nullable = false)
  private BigDecimal sellValue = BigDecimal.valueOf(-666);

  @NotNull()
  @Column(nullable = false)
  private String remarks = "";

  @NotNull(message = "Cost price of estimate position is required.")
  @Min(value = 0, message = "Price cannot be lower than 0.")
  @Column(nullable = false)
  private BigDecimal costPrice;

  @NotNull()
  @Column(nullable = false)
  private BigDecimal costValue = BigDecimal.valueOf(-666);
}

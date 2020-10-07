package machineRental.MR.price.delivery.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import machineRental.MR.material.model.Material;
import machineRental.MR.price.distance.service.DateCheckerObject;
import machineRental.MR.price.PriceType;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_prices")
public class DeliveryPrice extends DateCheckerObject {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "contractor_id")
  private Client contractor;

  @ManyToOne
  @JoinColumn(name = "material_id")
  private Material material;

  @NotNull(message = "Price type is required.")
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PriceType priceType;

  @NotNull
  @Column(nullable = false)
  private BigDecimal price;

  @NotNull
  @Column(nullable = false)
  private LocalDate startDate;

  @NotNull
  @Column(nullable = false)
  private LocalDate endDate;

  @NotNull
  @Column(nullable = false)
  private String projectCode;

}

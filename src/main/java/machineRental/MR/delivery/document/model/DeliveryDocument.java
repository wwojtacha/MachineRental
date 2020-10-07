package machineRental.MR.delivery.document.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_documents")
public class DeliveryDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotEmpty(message = "Delivery document number is required.")
  @Column(nullable = false, unique = true)
  private String documentNumber;

  @NotNull(message = "Delivery document date is required.")
  @Column(nullable = false)
  private LocalDate date;

}

package machineRental.MR.delivery.document.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryDocumentDto {

  private Long id;

  private String documentNumber;

  private LocalDate date;

}

package machineRental.MR.delivery.document.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import machineRental.MR.client.model.Client;

@Data
@AllArgsConstructor
public class DeliveryDocumentDto {

  private Long id;

  private Client contractor;

  private String documentNumber;

  private LocalDate date;

}

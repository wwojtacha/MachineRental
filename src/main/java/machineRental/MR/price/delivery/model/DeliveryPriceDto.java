package machineRental.MR.price.delivery.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machineRental.MR.client.model.Client;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.PriceType;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class DeliveryPriceDto {

  private Long id;

  private Client contractor;

  private Material material;

  private PriceType priceType;

  private BigDecimal price;

  private LocalDate startDate;

  private LocalDate endDate;

  private MultipartFile file;

  private String projectCode;

  private LocalDate modificationDate;

  public DeliveryPriceDto(final Long id, final Client contractor, final Material material, final PriceType priceType, final BigDecimal price, final LocalDate startDate, final LocalDate endDate, final String projectCode, final LocalDate modificationDate) {
    this.id = id;
    this.contractor = contractor;
    this.material = material;
    this.priceType = priceType;
    this.price = price;
    this.startDate = startDate;
    this.endDate = endDate;
    this.projectCode = projectCode;
    this.modificationDate = modificationDate;
  }

}

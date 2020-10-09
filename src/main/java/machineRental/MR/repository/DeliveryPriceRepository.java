package machineRental.MR.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import machineRental.MR.client.model.Client;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.delivery.model.DeliveryPrice;
import machineRental.MR.price.PriceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPriceRepository extends JpaRepository<DeliveryPrice, Long> {


  Page<DeliveryPrice> findByContractor_NameContainingAndMaterial_TypeContainingAndPriceTypeInAndProjectCodeContaining(String contractorName, String materialTYpe, List<PriceType> priceType, String projectCode, Pageable pageable);

  List<DeliveryPrice> findByContractor_MpkContaining(String contractorMpk);

  DeliveryPrice findByContractorAndMaterialAndPriceTypeInAndPriceAndStartDateAndEndDateAndProjectCode(
      Client contreactor,
      Material material,
      PriceType priceType,
      BigDecimal Price,
      LocalDate startDate,
      LocalDate endDate,
      String projectCode
      );

  List<DeliveryPrice> findAllByContractor(Client newDeliveryPriceContractor);

  List<DeliveryPrice> findByContractor_Mpk(String contractorMpk);

  List<DeliveryPrice> findByProjectCode(String projectCode);
}

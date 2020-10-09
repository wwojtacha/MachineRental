package machineRental.MR.repository;

import java.util.List;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDocumentEntryRepository extends JpaRepository<DeliveryDocumentEntry, Long> {


  List<DeliveryDocumentEntry> findByDeliveryDocument_DocumentNumber(String deliveryDocumentNumber);


  boolean existsByDeliveryPrice_Id(Long id);

  List<DeliveryDocumentEntry> findByContractor_MpkContaining(String contractorMpk);

  void deleteByDeliveryDocument_DocumentNumber(String documentNumber);

  List<DeliveryDocumentEntry> findAllByDeliveryPrice_Id(Long priceId);

  List<DeliveryDocumentEntry> findByEstimatePosition_CostCode_ProjectCode(String projectCode);

  List<DeliveryDocumentEntry> findByEstimatePosition_Id(Long estimateId);
}

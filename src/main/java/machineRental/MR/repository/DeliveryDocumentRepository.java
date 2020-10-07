package machineRental.MR.repository;

import java.time.LocalDate;
import machineRental.MR.client.model.Client;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDocumentRepository extends JpaRepository<DeliveryDocument, Long> {

  boolean existsByDocumentNumber(String documnentNumber);

  Page<DeliveryDocument> findByContractor_NameContainingAndDocumentNumberContainingAndDateEquals(String contractorName, String documentNumber, LocalDate date, Pageable pageable);

  Page<DeliveryDocument> findByContractor_NameContainingAndDocumentNumberContaining(String contractorName, String documentNumber, Pageable pageable);

  DeliveryDocument findByDocumentNumber(String documentNumber);

  boolean existsByContractorAndDocumentNumber(Client contractor, String documentNumber);

}

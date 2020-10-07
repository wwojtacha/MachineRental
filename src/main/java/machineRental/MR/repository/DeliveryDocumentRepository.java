package machineRental.MR.repository;

import java.time.LocalDate;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDocumentRepository extends JpaRepository<DeliveryDocument, Long> {

  boolean existsByDocumentNumber(String documnentNumber);

  Page<DeliveryDocument> findByDocumentNumberContainingAndDateEquals(String documentNumber, LocalDate date, Pageable pageable);

  Page<DeliveryDocument> findByDocumentNumberContaining(String documentNumber, Pageable pageable);

  DeliveryDocument findByDocumentNumber(String documentNumber);

}

package machineRental.MR.repository;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.workDocument.DocumentType;
import machineRental.MR.workDocument.model.WorkDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDocumentRepository extends JpaRepository<WorkDocument, String> {

  Page<WorkDocument> findByIdContainingAndDocumentTypeInAndDateEqualsAndOperator_NameContainingAndMachine_InternalIdContainingAndDelegationContainingAndInvoiceNumberContainingOrderByDate(
      String id,
      List<DocumentType> documentType,
      LocalDate date,
      String operatorName,
      String machineInternalId,
      String delegation,
      String invoiceNumber,
      Pageable pageable);

  Page<WorkDocument> findByIdContainingAndDocumentTypeInAndOperator_NameContainingAndMachine_InternalIdContainingAndDelegationContainingAndInvoiceNumberContainingOrderByDate(
      String id,
      List<DocumentType> documentType,
      String operatorName,
      String machineInternalId,
      String delegation,
      String invoiceNumber,
      Pageable pageable);

}

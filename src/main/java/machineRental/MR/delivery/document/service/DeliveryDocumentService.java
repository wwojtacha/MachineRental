package machineRental.MR.delivery.document.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import machineRental.MR.client.model.Client;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.delivery.document.model.DeliveryDocumentDto;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.repository.DeliveryDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class DeliveryDocumentService {


  @Autowired
  private DeliveryDocumentRepository deliveryDocumentRepository;

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

  public DeliveryDocumentDto create(DeliveryDocumentDto deliveryDocumentDto, BindingResult bindingResult) {

    validateDeliveryDocumentConsistency(convertToEntity(deliveryDocumentDto), null, bindingResult);

    DeliveryDocument deliveryDocument = convertToEntity(deliveryDocumentDto);
    deliveryDocumentRepository.save(deliveryDocument);

    return convertToDto(deliveryDocument);
  }

  private DeliveryDocument convertToEntity(DeliveryDocumentDto deliveryDocumentDto) {
    Long id = deliveryDocumentDto.getId();
    Client contractor = deliveryDocumentDto.getContractor();
    String documentNumber = deliveryDocumentDto.getDocumentNumber();
    LocalDate date = deliveryDocumentDto.getDate();
    DeliveryDocument deliveryDocument = new DeliveryDocument(id, contractor, documentNumber, date);

    return deliveryDocument;
  }

  private DeliveryDocumentDto convertToDto(DeliveryDocument deliveryDocument) {

    Long id = deliveryDocument.getId();
    Client contractor = deliveryDocument.getContractor();
    String documentNumber = deliveryDocument.getDocumentNumber();
    LocalDate date = deliveryDocument.getDate();

    DeliveryDocumentDto deliveryDocumentDto = new DeliveryDocumentDto(id, contractor, documentNumber, date);

    return deliveryDocumentDto;
  }

  public Page<DeliveryDocumentDto> search(String contractorName, String documentNumber, LocalDate date, Pageable pageable) {

    Page<DeliveryDocument> deliveryDocuments;

    if (date == null) {
      deliveryDocuments = deliveryDocumentRepository.findByContractor_NameContainingAndDocumentNumberContaining(contractorName, documentNumber, pageable);
    } else {
      deliveryDocuments = deliveryDocumentRepository.findByContractor_NameContainingAndDocumentNumberContainingAndDateEquals(contractorName, documentNumber, date, pageable);
    }

    return deliveryDocuments.map(this::convertToDto);
  }

  public DeliveryDocumentDto update(Long id, DeliveryDocumentDto deliveryDocumentDto, BindingResult bindingResult) {
    Optional<DeliveryDocument> dbDeliveryDocument = deliveryDocumentRepository.findById(id);
    if(!dbDeliveryDocument.isPresent()) {
      throw new NotFoundException(String.format("Delivery document with number: \'%s\' does not exist", deliveryDocumentDto.getDocumentNumber()));
    }

    DeliveryDocument deliveryDocument = convertToEntity(deliveryDocumentDto);
    validateDeliveryDocumentConsistency(deliveryDocument, dbDeliveryDocument.get(), bindingResult);

    deliveryDocumentEntryService.updateDeliveryDocumentEntryRegardingChangedDate(dbDeliveryDocument.get(), deliveryDocument);

    deliveryDocument.setId(id);
    deliveryDocumentRepository.save(deliveryDocument);
    return convertToDto(deliveryDocument);
  }

  private void validateDeliveryDocumentConsistency(DeliveryDocument editedDeliveryDocument, DeliveryDocument existingDeliveryDocument, BindingResult bindingResult) {

    Client editedDeliveryDocumentContractor = editedDeliveryDocument.getContractor();
    String editedDeliveryDocumentNumber = editedDeliveryDocument.getDocumentNumber();


    Client existingDeliveryDocumentContractor = new Client();
    String existingDeliveryDocumentNumber = "";

    if (existingDeliveryDocument != null) {
      existingDeliveryDocumentContractor = existingDeliveryDocument.getContractor();
      existingDeliveryDocumentNumber = existingDeliveryDocument.getDocumentNumber();
    }

    if(deliveryDocumentRepository.existsByContractorAndDocumentNumber(editedDeliveryDocumentContractor, editedDeliveryDocumentNumber)
        && (!editedDeliveryDocumentNumber.equals(existingDeliveryDocumentNumber)
        || !editedDeliveryDocumentContractor.equals(existingDeliveryDocumentContractor))) {
      bindingResult.addError(new FieldError(
          "DeliveryDocument",
          "documentNumber",
          String.format("Delivery document for contractor %s and number: \'%s\' already exists", editedDeliveryDocumentContractor.getName(), editedDeliveryDocumentNumber)));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void delete(Long id) {

    Optional<DeliveryDocument> dbDeliveryDocument = deliveryDocumentRepository.findById(id);

    if (!dbDeliveryDocument.isPresent()) {
      throw new NotFoundException(String.format("Delivery document with id \'%s\' doesn`t exist!", id));
    }

    deliveryDocumentEntryService.deleteByDeliveryDocument(dbDeliveryDocument.get().getDocumentNumber());

    deliveryDocumentRepository.deleteById(id);
  }

  public DeliveryDocumentDto getDeliveryDocument(String documentNumber) {
    if (!deliveryDocumentRepository.existsByDocumentNumber(documentNumber)) {
      throw new NotFoundException(String.format("Delivery document with id \'%s\' doesn`t exist!", documentNumber));
    }
    DeliveryDocument dbDeliveryDocument = deliveryDocumentRepository.findByDocumentNumber(documentNumber);

    return convertToDto(dbDeliveryDocument);
  }

}

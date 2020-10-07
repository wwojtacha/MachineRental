package machineRental.MR.delivery.document.service;

import java.time.LocalDate;
import java.util.Optional;
import javax.transaction.Transactional;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.delivery.document.model.DeliveryDocumentDto;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
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

    validateDeliveryDocumentConsistency(deliveryDocumentDto.getDocumentNumber(), null, bindingResult);

    DeliveryDocument deliveryDocument = convertToEntity(deliveryDocumentDto);
    deliveryDocumentRepository.save(deliveryDocument);

    return convertToDto(deliveryDocument);
  }

  private DeliveryDocument convertToEntity(DeliveryDocumentDto deliveryDocumentDto) {
    Long id = deliveryDocumentDto.getId();
    String documentNumber = deliveryDocumentDto.getDocumentNumber();
    LocalDate date = deliveryDocumentDto.getDate();
    DeliveryDocument deliveryDocument = new DeliveryDocument(id, documentNumber, date);

    return deliveryDocument;
  }

  private DeliveryDocumentDto convertToDto(DeliveryDocument deliveryDocument) {

    Long id = deliveryDocument.getId();
    String documentNumber = deliveryDocument.getDocumentNumber();
    LocalDate date = deliveryDocument.getDate();

    DeliveryDocumentDto deliveryDocumentDto = new DeliveryDocumentDto(id, documentNumber, date);

    return deliveryDocumentDto;
  }

  public Page<DeliveryDocumentDto> search(String documentNumber, LocalDate date, Pageable pageable) {

    Page<DeliveryDocument> deliveryDocuments;

    if (date == null) {
      deliveryDocuments = deliveryDocumentRepository.findByDocumentNumberContaining(documentNumber, pageable);
    } else {
      deliveryDocuments = deliveryDocumentRepository.findByDocumentNumberContainingAndDateEquals(documentNumber, date, pageable);
    }

    return deliveryDocuments.map(this::convertToDto);
  }

  public DeliveryDocumentDto update(Long id, DeliveryDocumentDto deliveryDocumentDto, BindingResult bindingResult) {
    Optional<DeliveryDocument> dbDeliveryDocument = deliveryDocumentRepository.findById(id);
    if(!dbDeliveryDocument.isPresent()) {
      throw new NotFoundException(String.format("Delivery document with number: \'%s\' does not exist", deliveryDocumentDto.getDocumentNumber()));
    }

    validateDeliveryDocumentConsistency(deliveryDocumentDto.getDocumentNumber(), dbDeliveryDocument.get().getDocumentNumber(), bindingResult);

    DeliveryDocument deliveryDocument = convertToEntity(deliveryDocumentDto);

    deliveryDocumentEntryService.updateDeliveryDocumentEntryRegardingChangedDate(dbDeliveryDocument.get(), deliveryDocument);

    deliveryDocument.setId(id);
    deliveryDocumentRepository.save(deliveryDocument);
    return convertToDto(deliveryDocument);
  }

  private void validateDeliveryDocumentConsistency(String documentNumber, String currentDocumentNumber, BindingResult bindingResult) {
    if(deliveryDocumentRepository.existsByDocumentNumber(documentNumber) && !documentNumber.equals(currentDocumentNumber)) {
      bindingResult.addError(new FieldError(
          "DeliveryDocument",
          "documentNumber",
          String.format("Delivery document with number: \'%s\' already exists", documentNumber)));
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

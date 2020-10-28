package machineRental.MR.delivery.entry.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import machineRental.MR.client.model.Client;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.delivery.document.service.DeliveryDocumentService;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntryDto;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.delivery.DeliveryPriceChecker;
import machineRental.MR.price.delivery.model.DeliveryPrice;
import machineRental.MR.price.delivery.model.DeliveryPriceDto;
import machineRental.MR.price.delivery.service.DeliveryPriceService;
import machineRental.MR.repository.DeliveryDocumentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class DeliveryDocumentEntryService {

  private static final long CREATION_MODE_DELIVERY_DEOCUMENT_ENTRY_ID = -1;

  @Autowired
  private DeliveryDocumentEntryRepository deliveryDocumentEntryRepository;

  @Autowired
  private DeliveryPriceService deliveryPriceService;

  @Autowired
  private DeliveryDocumentService deliveryDocumentService;

  @Autowired
  private DeliveryPriceChecker deliveryPriceChecker;

  public DeliveryDocumentEntryDto create(DeliveryDocumentEntryDto deliveryDocumentEntryDto, BindingResult bindingResult) {

    validateDeliveryDocumentEntryConsistency(CREATION_MODE_DELIVERY_DEOCUMENT_ENTRY_ID, null, bindingResult);

    DeliveryDocumentEntry deliveryDocumentEntry = convertToEntity(deliveryDocumentEntryDto);

    DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
    if (deliveryDocument.getId() == null) {
      Long deliveryDocumentId = deliveryDocumentService.getDeliveryDocument(deliveryDocument.getDocumentNumber()).getId();
      deliveryDocument.setId(deliveryDocumentId);
    }

    deliveryDocumentEntryRepository.save(deliveryDocumentEntry);

    return convertToDto(deliveryDocumentEntry);
  }

  public DeliveryDocumentEntryDto update(Long id, DeliveryDocumentEntryDto deliveryDocumentEntryDto, BindingResult bindingResult) {
    Optional<DeliveryDocumentEntry> dbDeliveryDocumentEntry = deliveryDocumentEntryRepository.findById(id);
    if(!dbDeliveryDocumentEntry.isPresent()) {
      throw new NotFoundException(String.format("Delivery document entry with id: \'%s\' does not exist", deliveryDocumentEntryDto.getId()));
    }

    validateDeliveryDocumentEntryConsistency(id, dbDeliveryDocumentEntry.get().getId(), bindingResult);

    DeliveryDocumentEntry deliveryDocumentEntry = convertToEntity(deliveryDocumentEntryDto);

    deliveryDocumentEntry.setId(id);
    deliveryDocumentEntryRepository.save(deliveryDocumentEntry);
    return convertToDto(deliveryDocumentEntry);
  }

  private void validateDeliveryDocumentEntryConsistency(Long id, Long currentId, BindingResult bindingResult) {
    if(deliveryDocumentEntryRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "client",
          "mpk",
          String.format("Delivery document enry with id: \'%s\' already exists", id)));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public List<DeliveryDocumentEntryDto> getAllEntriesFor(String deliveryDocumentNumber) {
    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryRepository.findByDeliveryDocument_DocumentNumber(deliveryDocumentNumber);

    return deliveryDocumentEntries.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public void delete(Long id) {

    Optional<DeliveryDocumentEntry> dbDeliveryDocument = deliveryDocumentEntryRepository.findById(id);

    if (!dbDeliveryDocument.isPresent()) {
      throw new NotFoundException(String.format("Delivery document entry with id \'%s\' doesn`t exist!", id));
    }

    deliveryDocumentEntryRepository.deleteById(id);
  }

  private DeliveryDocumentEntry convertToEntity(DeliveryDocumentEntryDto deliveryDocumentEntryDto) {
    Long id = deliveryDocumentEntryDto.getId();
    Client contractor = deliveryDocumentEntryDto.getContractor();
    Material material = deliveryDocumentEntryDto.getMaterial();
    double quantity = deliveryDocumentEntryDto.getQuantity();
    String measureUnit = deliveryDocumentEntryDto.getMeasureUnit();
    EstimatePosition estimatePosition = deliveryDocumentEntryDto.getEstimatePosition();
    CostCode costCode = deliveryDocumentEntryDto.getCostCode();
    DeliveryPrice deliveryPrice = deliveryDocumentEntryDto.getDeliveryPrice();
    String invoiceNumber = deliveryDocumentEntryDto.getInvoiceNumber();
    DeliveryDocument deliveryDocument = deliveryDocumentEntryDto.getDeliveryDocument();

    DeliveryDocumentEntry deliveryDocumentEntry = new DeliveryDocumentEntry(id, contractor, material, quantity, measureUnit, estimatePosition, costCode, deliveryPrice, invoiceNumber, deliveryDocument);

    return deliveryDocumentEntry;
  }

  private DeliveryDocumentEntryDto convertToDto(DeliveryDocumentEntry deliveryDocumentEntry) {

    Long id = deliveryDocumentEntry.getId();
    Client contractor = deliveryDocumentEntry.getContractor();
    Material material = deliveryDocumentEntry.getMaterial();
    double quantity = deliveryDocumentEntry.getQuantity();
    String measureUnit = deliveryDocumentEntry.getMeasureUnit();
    EstimatePosition estimatePosition = deliveryDocumentEntry.getEstimatePosition();
    CostCode costCode = deliveryDocumentEntry.getCostCode();
    DeliveryPrice deliveryPrice = deliveryDocumentEntry.getDeliveryPrice();
    BigDecimal costValue = BigDecimal.valueOf(quantity).multiply(deliveryPrice.getPrice());
    String invoiceNumber = deliveryDocumentEntry.getInvoiceNumber();
    DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
    DeliveryDocumentEntryDto deliveryDocumentEntryDto = new DeliveryDocumentEntryDto(id, contractor, material, quantity, measureUnit, estimatePosition, costCode, deliveryPrice, costValue, invoiceNumber, deliveryDocument);

    return deliveryDocumentEntryDto;
  }

  public void updateDeliveryDocumentEntryRegardingChangedDate(DeliveryDocument dbDeliveryDocument, DeliveryDocument editedDeliveryDocument) {
    String documentNumber = dbDeliveryDocument.getDocumentNumber();
    LocalDate editedDate = editedDeliveryDocument.getDate();

    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryRepository.findByDeliveryDocument_DocumentNumber(documentNumber);

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {
      String mpk = deliveryDocumentEntry.getContractor().getMpk();
      List<DeliveryPriceDto> matchingPrices = deliveryPriceService.getMatchingPrices(mpk, editedDate);

      boolean isMatchingPrice = false;

      MATCHING_PRICES:
      for (DeliveryPriceDto matchingPriceDto : matchingPrices) {
        DeliveryPrice matchingPrice = deliveryPriceService.convertToEntity(matchingPriceDto);

        DeliveryPrice deliveryPrice = deliveryDocumentEntry.getDeliveryPrice();

        if (deliveryPriceService.isPriceMatching(editedDate, deliveryPrice, matchingPrice)) {
          deliveryDocumentEntry.setDeliveryPrice(matchingPrice);
          isMatchingPrice = true;
          break MATCHING_PRICES;
        }
      }

      String materialType = deliveryDocumentEntry.getMaterial().getType();
      PriceType priceType = deliveryDocumentEntry.getDeliveryPrice().getPriceType();
      String projectCode = deliveryDocumentEntry.getCostCode().getProjectCode();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no delivery price matching editedDate %s and price paramters: %s, %s, %s, %s.", editedDate, mpk, materialType, priceType, projectCode));
      }

    }
  }

  @Transactional
  public void deleteByDeliveryDocument(String deliveryDocumentNumber) {

    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryRepository.findByDeliveryDocument_DocumentNumber(deliveryDocumentNumber);

    if (deliveryDocumentEntries.isEmpty()) {
      return;
    }

    deliveryDocumentEntryRepository.deleteByDeliveryDocument_DocumentNumber(deliveryDocumentNumber);
  }

  public List<DeliveryDocumentEntry> getDeliveryDocumentEntriesByDeliveryPrice(Long priceId) {
    return deliveryDocumentEntryRepository.findAllByDeliveryPrice_Id(priceId);
  }

  public void updateOnEstimatePositionChange(Long id, EstimatePosition editedEstimatePosition) {

    List<DeliveryDocumentEntry> deliveryDocumentEntries = getDeliveryDocumentEntriesByEstimatePosition_Id(id);

    if (deliveryDocumentEntries.isEmpty()) {
      return;
    }

    String editedEstimateProjectCode = editedEstimatePosition.getCostCode().getProjectCode();

    List<DeliveryPrice> deliveryPricesByProjectCode = deliveryPriceService.getDeliveryPricesByProjectCode(editedEstimateProjectCode);


    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {

      boolean isMatchingPrice = false;

      for (DeliveryPrice deliveryPrice : deliveryPricesByProjectCode) {
        if (deliveryPriceChecker.isPriceMatchingEditedEstimateProjectCode(deliveryDocumentEntry, deliveryPrice)) {
          deliveryDocumentEntry.setDeliveryPrice(deliveryPrice);
          editedEstimatePosition.setId(id);
          deliveryDocumentEntry.setEstimatePosition(editedEstimatePosition);
//          workReportEntryRepository.save(deliveryDocumentEntry);
          isMatchingPrice = true;
          break;
        }
      }

      DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
      String contractorMpk = deliveryDocumentEntry.getContractor().getMpk();
      String materalType = deliveryDocumentEntry.getMaterial().getType();
      PriceType priceType = deliveryDocumentEntry.getDeliveryPrice().getPriceType();
      LocalDate date = deliveryDocument.getDate();

      if (!isMatchingPrice) {
        throw new NotFoundException(String.format("There is no delivery price matching edited estimate project code %s and delivery document entry parameters: %s, %s, %s, %s.",
            editedEstimateProjectCode,
            contractorMpk,
            materalType,
            priceType,
            date));
      }

    }

  }

  public List<DeliveryDocumentEntry> getDeliveryDocumentEntriesByEstimatePosition_Id(Long estimateId) {
    return deliveryDocumentEntryRepository.findByEstimatePosition_Id(estimateId);
  }


  private List<DeliveryDocumentEntry> getWorkReportEntriesByEstimateProjectCode(String projectCode) {
    return deliveryDocumentEntryRepository.findByEstimatePosition_CostCode_ProjectCode(projectCode);
  }

  public List<DeliveryDocumentEntry> getDeliveryDocumentEntriesBetweenDates(LocalDate startDate, LocalDate endDate) {
    return deliveryDocumentEntryRepository.findByDeliveryDocument_DateBetween(startDate, endDate);
  }

  public List<DeliveryDocumentEntry> getDeliveryDocumentEntriesBetweenDatesByEstimateProjectCode(LocalDate startDate, LocalDate endDate, String projectCode) {
    return deliveryDocumentEntryRepository.findByDeliveryDocument_DateBetweenAndEstimatePosition_CostCode_ProjectCodeEquals(startDate, endDate, projectCode);
  }
}

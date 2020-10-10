package machineRental.MR.price.delivery.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import machineRental.MR.client.model.Client;
import machineRental.MR.delivery.document.model.DeliveryDocument;
import machineRental.MR.delivery.entry.model.DeliveryDocumentEntry;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import machineRental.MR.excel.ExcelHelper;
import machineRental.MR.excel.WrongDataTypeException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.material.model.Material;
import machineRental.MR.price.delivery.DeliveryPriceChecker;
import machineRental.MR.price.delivery.model.DeliveryPrice;
import machineRental.MR.price.delivery.model.DeliveryPriceDto;
import machineRental.MR.price.delivery.model.DoubleDeliveryPrice;
import machineRental.MR.price.distance.service.DateChecker;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.hour.exception.IncorrectDateException;
import machineRental.MR.price.hour.exception.NothingChangedException;
import machineRental.MR.price.hour.exception.OverlappingDatesException;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.repository.ClientRepository;
import machineRental.MR.repository.CostCodeRepository;
import machineRental.MR.repository.DeliveryDocumentEntryRepository;
import machineRental.MR.repository.DeliveryPriceRepository;
import machineRental.MR.repository.MaterialRepository;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DeliveryPriceService {

  @Autowired
  private DeliveryPriceRepository deliveryPriceRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private MaterialRepository materialRepository;

  @Autowired
  private CostCodeRepository costCodeRepository;

  @Autowired
  private DeliveryPriceChecker deliveryPriceChecker;

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;

  @Autowired
  private DeliveryDocumentEntryRepository deliveryDocumentEntryRepository;

  private DateChecker dateChecker = new DateChecker();

  @Autowired
  private ExcelHelper excelHelper;

  public List<DeliveryPrice> findAll() {
    return (List<DeliveryPrice>) deliveryPriceRepository.findAll();
  }

  public void saveDataFromExcelFile(MultipartFile file) {

    List<DeliveryPrice> prices = readDataFromExcel(file);

    if (isPriceUnique(file)) {
      for (DeliveryPrice price : prices) {
//        createPriceId(price);
        deliveryPriceRepository.save(price);
      }
    }
  }

  public List<DeliveryPrice> readDataFromExcel(MultipartFile file) {

    final List<DeliveryPrice> deliveryPricesFromExcelFile = new ArrayList<>();

    if (excelHelper.isProperFileType(file)) {
      Workbook workbook = excelHelper.getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
          String contractorMpk = row.getCell(0).getStringCellValue();

          if (!clientRepository.existsByMpk(contractorMpk)) {
            throw new NotFoundException(String.format("Contractor with NIP \'%s\' does not exist.", contractorMpk));
          }

          Client contractor = clientRepository.findByMpk(contractorMpk);
          deliveryPrice.setContractor(contractor);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'contractor NIP\'. It must be a string (text).");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String materialType = String.valueOf(row.getCell(1));

          if (!materialRepository.existsByType(materialType)) {
            throw new NotFoundException(String.format("Material \'%s\' does not exist.", materialType));
          }

          Material material = materialRepository.findByType(materialType);

          deliveryPrice.setMaterial(material);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'material type\'. It must be a string (text)!");
        }

        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
          String priceType = String.valueOf(row.getCell(2));

          if (!EnumUtils.isValidEnum(PriceType.class, priceType)) {
            throw new NotFoundException(String.format("Price type \'%s\' does not exist.", priceType));
          }
          deliveryPrice.setPriceType(Enum.valueOf(PriceType.class, priceType));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'sellPrice\'. It must be a number!");
        }

        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(3).getNumericCellValue();
          BigDecimal price = BigDecimal.valueOf(cellDoubleValue);
          deliveryPrice.setPrice(price);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'price\'. It must be a number!");
        }

        LocalDate startDate;
        if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(4).getDateCellValue();
          startDate = convertToLocalDateViaInstant(cellDateValue);
          deliveryPrice.setStartDate(startDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'startDate\'. It must be a Date!");
        }

        if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(5).getDateCellValue();
          LocalDate endDate = convertToLocalDateViaInstant(cellDateValue);

          if (endDate.isBefore(startDate)) {
            throw new IncorrectDateException("End date must be equal or greater than start date.");
          }

          deliveryPrice.setEndDate(endDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'endDate\'. It must be a Date!");
        }

        if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
          String projectCode = row.getCell(6).getStringCellValue();

          if (!costCodeRepository.existsByProjectCode(projectCode)) {
            throw new NotFoundException(String.format("Project code \'%s\' does not exist.", projectCode));
          }

          deliveryPrice.setProjectCode(projectCode);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'project code\'. It must be a string (text).");
        }

//        createPriceId(deliveryPrice);
        deliveryPricesFromExcelFile.add(deliveryPrice);
      }
    }
    return deliveryPricesFromExcelFile;
  }

  public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  private boolean isPriceUnique(MultipartFile file) {

    boolean isUnique = true;
    final List<DeliveryPrice> pricesFromDb = findAll();
    final List<DeliveryPrice> pricesFromExcelFile = readDataFromExcel(file);
    final List<DeliveryPrice> pricesFromDbAndExcelFile = new ArrayList<>();
    pricesFromDbAndExcelFile.addAll(pricesFromDb);
    pricesFromDbAndExcelFile.addAll(pricesFromExcelFile);

    for (DeliveryPrice checkedPrice : pricesFromDbAndExcelFile) {

      for (DeliveryPrice price : pricesFromDbAndExcelFile) {

        if (checkedPrice == price) {
          continue;
        }

        if (!deliveryPriceChecker.isPriceUnique(checkedPrice, price)) {
          isUnique = false;
          throw new OverlappingDatesException(String.format("Delivery price for a given contractor (%s), material (%s), price type (%s) and project code (%s) cannot overlap in time with the same entry.",
              checkedPrice.getContractor().getMpk(), checkedPrice.getMaterial().getType(), checkedPrice.getPriceType().toString(), checkedPrice.getProjectCode()));
        }
      }
    }

    return isUnique;
  }

  public DeliveryPrice convertToEntity(DeliveryPriceDto deliveryPriceDto) {
    Long id = deliveryPriceDto.getId();
    Client contractor = deliveryPriceDto.getContractor();
    Material material = deliveryPriceDto.getMaterial();
    PriceType priceType = deliveryPriceDto.getPriceType();
    BigDecimal price = deliveryPriceDto.getPrice();
    LocalDate startDate = deliveryPriceDto.getStartDate();
    LocalDate endDate = deliveryPriceDto.getEndDate();
    String projectCode = deliveryPriceDto.getProjectCode();
    DeliveryPrice deliveryPrice = new DeliveryPrice(id, contractor, material, priceType, price, startDate, endDate, projectCode);

    return deliveryPrice;
  }

  private DeliveryPriceDto convertToDto(DeliveryPrice deliveryPrice) {

    Long id = deliveryPrice.getId();
    Client contractor = deliveryPrice.getContractor();
    Material material = deliveryPrice.getMaterial();
    PriceType priceType = deliveryPrice.getPriceType();
    BigDecimal price = deliveryPrice.getPrice();
    LocalDate startDate = deliveryPrice.getStartDate();
    LocalDate endDate = deliveryPrice.getEndDate();
    String projectCode = deliveryPrice.getProjectCode();
    DeliveryPriceDto deliveryPriceDto = new DeliveryPriceDto(id, contractor, material, priceType, price, startDate, endDate, projectCode);

    return deliveryPriceDto;
  }

  public DeliveryPriceDto update(Long id, DeliveryPriceDto editedDeliveryPriceDto) {

    Optional<DeliveryPrice> dbPriceOptional = deliveryPriceRepository.findById(id);
    if (!dbPriceOptional.isPresent()) {
      throw new NotFoundException(String.format("Delivery price with id \'%s\' doesn`t exist.", id));
    }

    DeliveryPrice editedDeliveryPrice = convertToEntity(editedDeliveryPriceDto);

//    if (!isOnlyPriceValueDifferent(dbPriceOptional.get(), editedDeliveryPrice)) {
//      deliveryPriceChecker.checkPriceUsage(id);
//    }

    validateDeliveryPriceConsistency(id, editedDeliveryPrice);

    deliveryPriceChecker.checkEditability(id, dbPriceOptional.get(), editedDeliveryPrice);

    editedDeliveryPrice.setId(id);
    deliveryPriceRepository.save(editedDeliveryPrice);

    return convertToDto(editedDeliveryPrice);
  }

  private boolean isOnlyPriceValueDifferent (DeliveryPrice dbPrice, DeliveryPrice editedPrice) {
    return dbPrice.getContractor().equals(editedPrice.getContractor())
        && dbPrice.getMaterial().equals(editedPrice.getMaterial())
        && dbPrice.getPriceType() == editedPrice.getPriceType()
        && dbPrice.getStartDate().isEqual(editedPrice.getStartDate())
        && dbPrice.getEndDate().isEqual(editedPrice.getEndDate())
        && dbPrice.getProjectCode().equals(editedPrice.getProjectCode())
        && !dbPrice.getPrice().equals(editedPrice.getPrice());
  }

  private void validateDeliveryPriceConsistency(Long id, DeliveryPrice deliveryPrice) {
    if (!isPriceUnique(id, deliveryPrice)) {
      throw new OverlappingDatesException(String.format("Delivery price for a given contractor (%s), material (%s), price type (%s) and project code (%s) cannot overlap in time with the same entry.",
          deliveryPrice.getContractor().getMpk(), deliveryPrice.getMaterial().getType(), deliveryPrice.getPriceType().toString(), deliveryPrice.getProjectCode()));
    }
  }

  private boolean isPriceUnique(Long id, DeliveryPrice editedPrice) {
    boolean isUnique = true;
    final List<DeliveryPrice> pricesFromDb = findAll();

    for (DeliveryPrice dbPrice : pricesFromDb) {

      if (id == dbPrice.getId()) {
        continue;
      }

      if (!deliveryPriceChecker.isPriceUnique(editedPrice, dbPrice)) {
        isUnique = false;
        throw new OverlappingDatesException(
            String.format("Delivery price for a given contractor (%s), material (%s), price type (%s) and project code (%s) cannot overlap in time with the same entry.",
                editedPrice.getContractor().getMpk(), editedPrice.getMaterial().getType(), editedPrice.getPriceType().toString(), editedPrice.getProjectCode()));
      }

    }

    return isUnique;
  }

  public Page<DeliveryPriceDto> search(String contractorName, String materialType, List<PriceType> priceType, String projectCode, Pageable pageable) {

    Page<DeliveryPrice> deliveryPrices;

    if (isEmpty(priceType)) {
      priceType = new ArrayList<>(EnumSet.allOf(PriceType.class));
    }

    deliveryPrices = deliveryPriceRepository.findByContractor_NameContainingAndMaterial_TypeContainingAndPriceTypeInAndProjectCodeContaining(contractorName, materialType, priceType, projectCode, pageable);

    return deliveryPrices.map(this::convertToDto);
  }

  public List<DeliveryPriceDto> getMatchingPrices(String contractorMpk, LocalDate date) {
    List<DeliveryPrice> result = new ArrayList<>();

    List<DeliveryPrice> deliveryPrices = deliveryPriceRepository.findByContractor_MpkContaining(contractorMpk);

    for (DeliveryPrice price : deliveryPrices) {
      if ((date.isAfter(price.getStartDate()) || date.isEqual(price.getStartDate()))
          && (date.isBefore(price.getEndDate()) || date.isEqual(price.getEndDate()))) {

        result.add(price);
      }
    }

    return result.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public boolean isPriceMatching(LocalDate date, DeliveryPrice newPrice, DeliveryPrice price) {
    return newPrice.getContractor().equals(price.getContractor())
        && newPrice.getMaterial().equals(price.getMaterial())
        && newPrice.getPriceType() == price.getPriceType()
        && newPrice.getProjectCode().equals(price.getProjectCode())
        && (date.isAfter(price.getStartDate()) || date.isEqual(price.getStartDate()))
        && (date.isBefore(price.getEndDate()) || date.isEqual(price.getEndDate()));
  }

  public void updateOnDoubleChange(Long id, DoubleDeliveryPrice doubleDeliveryPrice) {

    DeliveryPrice editedDeliveryPrice = doubleDeliveryPrice.getEditedDeliveryPrice();
    DeliveryPrice newDeliveryPrice = doubleDeliveryPrice.getNewDeliveryPrice();

    Optional<DeliveryPrice> dbPriceOptional = deliveryPriceRepository.findById(id);
    if (!dbPriceOptional.isPresent()) {
      throw new NotFoundException(String.format("Delivery price with id \'%s\' doesn`t exist.", id));
    }

    dateChecker.checkEndDateAfterStartDate(editedDeliveryPrice);
    dateChecker.checkEndDateAfterStartDate(newDeliveryPrice);

    DeliveryPrice dbPrice = dbPriceOptional.get();

    if (dateChecker.areSameDates(editedDeliveryPrice, dbPrice)) {
      throw new NothingChangedException("Dates haven`t been changed. Nothing has been updated.");
    }

    if (dateChecker.areDatesOverlapping(newDeliveryPrice, editedDeliveryPrice)) {
      throw new OverlappingDatesException("Dates cannot overlap in time.");
    }

    List<DeliveryDocumentEntry> deliveryDocumentEntries = deliveryDocumentEntryService.getDeliveryDocumentEntriesByDeliveryPrice(id);
    Client newDeliveryPriceContractor = newDeliveryPrice.getContractor();
    List<DeliveryPrice> allDeliveryPricesByContractor = deliveryPriceRepository.findAllByContractor(newDeliveryPriceContractor);

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {
      DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
      LocalDate date = deliveryDocument.getDate();


      if (dateChecker.isDateMatching(date, editedDeliveryPrice)) {
        for (DeliveryPrice dbDelievryPrice : allDeliveryPricesByContractor) {

          if (dbPrice == dbDelievryPrice) {
            continue;
          }

          if (!deliveryPriceChecker.isPriceUnique(editedDeliveryPrice, dbDelievryPrice)) {
            throw new OverlappingDatesException("Edited delivery price cannot overlap in time with the same entry.");
          }
        }
      } else if (dateChecker.isDateMatching(date, newDeliveryPrice)) {

        for (DeliveryPrice dbDeliveryPrice : allDeliveryPricesByContractor) {

          if (dbPrice == dbDeliveryPrice) {
            continue;
          }

          if (!deliveryPriceChecker.isPriceUnique(newDeliveryPrice, dbDeliveryPrice)) {
            throw new OverlappingDatesException("New delivery price cannot overlap in time with the same entry.");
          }
        }
      } else {
        throw new NotFoundException(String.format("There is no delivery price matching delivery document: %s position: %s, %s, %s, %s, %s",
            deliveryDocument.getDocumentNumber(),
            deliveryDocumentEntry.getContractor().getMpk(),
            deliveryDocumentEntry.getMaterial().getType(),
            deliveryDocumentEntry.getMeasureUnit(),
            deliveryDocumentEntry.getQuantity(),
            deliveryDocumentEntry.getCostCode().getFullCode()));
      }
    }

    Material newDeliveryPriceMaterial = newDeliveryPrice.getMaterial();
    PriceType newDeliveryPricePriceType = newDeliveryPrice.getPriceType();
    BigDecimal newDeliveryPricePrice = newDeliveryPrice.getPrice();
    LocalDate newDeliveryPriceStartDate = newDeliveryPrice.getStartDate();
    LocalDate newDeliveryPriceEndDate = newDeliveryPrice.getEndDate();
    String newDeliveryPriceProjectCode = newDeliveryPrice.getProjectCode();

    deliveryPriceRepository.save(newDeliveryPrice);

    DeliveryPrice newDeliveryPriceFromDb = deliveryPriceRepository
        .findByContractorAndMaterialAndPriceTypeInAndPriceAndStartDateAndEndDateAndProjectCode(
            newDeliveryPriceContractor, newDeliveryPriceMaterial, newDeliveryPricePriceType, newDeliveryPricePrice, newDeliveryPriceStartDate, newDeliveryPriceEndDate, newDeliveryPriceProjectCode
        );

    editedDeliveryPrice.setId(id);
    deliveryPriceRepository.save(editedDeliveryPrice);
    Optional<DeliveryPrice> editedDeliveryPriceFromDb = deliveryPriceRepository.findById(id);

    for (DeliveryDocumentEntry deliveryDocumentEntry : deliveryDocumentEntries) {
      DeliveryDocument deliveryDocument = deliveryDocumentEntry.getDeliveryDocument();
      LocalDate date = deliveryDocument.getDate();

      if (dateChecker.isDateMatching(date, editedDeliveryPrice)) {

        deliveryDocumentEntry.setDeliveryPrice(editedDeliveryPriceFromDb.get());
        deliveryDocumentEntryRepository.save(deliveryDocumentEntry);
      } else if (dateChecker.isDateMatching(date, newDeliveryPrice)) {

        deliveryDocumentEntry.setDeliveryPrice(newDeliveryPriceFromDb);
        deliveryDocumentEntryRepository.save(deliveryDocumentEntry);
      } else {
        return;
      }
    }

  }

  public List<DeliveryPrice> getDeliveryPricesByProjectCode(String projectCode) {
    return deliveryPriceRepository.findByProjectCode(projectCode);
  }

  public void delete(Long id) {
    Optional<DeliveryPrice> dbDeliveryPrice = deliveryPriceRepository.findById(id);

    if (!dbDeliveryPrice.isPresent()) {
      throw new NotFoundException(String.format("Delivery price with id \'%s\' doesn`t exist!", id));
    }

    deliveryPriceChecker.checkPriceUsage(id);

// if price is not used in any road card entry it can be deleted
    deliveryPriceRepository.deleteById(id);
  }

}

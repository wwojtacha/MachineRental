package machineRental.MR.price.distance.service;

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
import machineRental.MR.excel.ExcelHelper;
import machineRental.MR.excel.WrongDataTypeException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.price.distance.DistancePriceChecker;
import machineRental.MR.price.distance.exception.OverlappingDistanceRangesException;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.distance.model.DoubleDistancePrice;
import machineRental.MR.price.hour.exception.IncorrectDateException;
import machineRental.MR.price.hour.exception.NothingChangedException;
import machineRental.MR.price.hour.exception.OverlappingDatesException;
import machineRental.MR.repository.CostCodeRepository;
import machineRental.MR.repository.DistancePriceRepository;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.RoadCardEntryRepository;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
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
public class DistancePriceService {

  @Autowired
  private DistancePriceRepository distancePriceRepository;

  @Autowired
  private MachineRepository machineRepository;

  @Autowired
  private ExcelHelper excelHelper;

  @Autowired
  private DistancePriceChecker distancePriceChecker;

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  @Autowired
  private RoadCardEntryRepository roadCardEntryRepository;

  @Autowired
  private CostCodeRepository costCodeRepository;

  private DateChecker dateChecker = new DateChecker();

  public List<DistancePrice> findAll() {
    return (List<DistancePrice>) distancePriceRepository.findAll();
  }

  public void saveDataFromExcelFile(MultipartFile file) {

    List<DistancePrice> prices = readDataFromExcel(file);

    if (isPriceUnique(file)) {
      for (DistancePrice price : prices) {
//        createPriceId(price);
        distancePriceRepository.save(price);
      }
    }
  }

  public List<DistancePrice> readDataFromExcel(MultipartFile file) {

    final List<DistancePrice> hourPricesFromExcelFile = new ArrayList<>();

    if (excelHelper.isProperFileType(file)) {
      Workbook workbook = excelHelper.getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        DistancePrice distancePrice = new DistancePrice();
        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
          String workCode = row.getCell(0).getStringCellValue();

          if (!EnumUtils.isValidEnum(WorkCode.class, workCode)) {
            throw new NotFoundException(String.format("Work code \'%s\' does not exist.", workCode));
          }
          distancePrice.setWorkCode(Enum.valueOf(WorkCode.class, workCode));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'work code\'. It must be a valid work code (eg. PS).");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String machineInternalId = String.valueOf(row.getCell(1));
          if (!machineRepository.existsByInternalId(machineInternalId)) {
            throw new NotFoundException(String.format("Machine with internal ID \'%s\' does not exist.", machineInternalId));
          }

          Machine machine = machineRepository.findByInternalId(machineInternalId);
          distancePrice.setMachine(machine);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'machineInternalId\'. It must be a string (text)!");
        }

        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
          String priceType = String.valueOf(row.getCell(2));

          if (!EnumUtils.isValidEnum(PriceType.class, priceType)) {
            throw new NotFoundException(String.format("Price type \'%s\' does not exist.", priceType));
          }
          distancePrice.setPriceType(Enum.valueOf(PriceType.class, priceType));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'priceType\'. It must be a number!");
        }

        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(3).getNumericCellValue();
          BigDecimal price = BigDecimal.valueOf(cellDoubleValue);
          distancePrice.setPrice(price);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'price\'. It must be a number!");
        }

        if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double rangeMin = row.getCell(4).getNumericCellValue();
          distancePrice.setRangeMin(rangeMin);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'rangeMin\'. It must be a number!");
        }

        if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double rangeMax = row.getCell(5).getNumericCellValue();
          distancePrice.setRangeMax(rangeMax);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'rangeMax\'. It must be a number!");
        }

        LocalDate startDate;
        if (row.getCell(6).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(6).getDateCellValue();
          startDate = convertToLocalDateViaInstant(cellDateValue);
          distancePrice.setStartDate(startDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'startDate\'. It must be a Date!");
        }

        if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(7).getDateCellValue();
          LocalDate endDate = convertToLocalDateViaInstant(cellDateValue);

          if (endDate.isBefore(startDate)) {
            throw new IncorrectDateException("End date must be equal or greater than start date.");
          }

          distancePrice.setEndDate(endDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'endDate\'. It must be a Date!");
        }

        if (row.getCell(8).getCellType() == Cell.CELL_TYPE_STRING) {
          String projectCode = row.getCell(8).getStringCellValue();

          if (!costCodeRepository.existsByProjectCode(projectCode)) {
            throw new NotFoundException(String.format("Project code \'%s\' does not exist.", projectCode));
          }

          distancePrice.setProjectCode(projectCode);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'project code\'. It must be a string (text).");
        }

//        createPriceId(distancePrice);
        hourPricesFromExcelFile.add(distancePrice);
      }
    }
    return hourPricesFromExcelFile;
  }

  public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  private boolean isPriceUnique(MultipartFile file) {

    boolean isUnique = true;
    final List<DistancePrice> pricesFromDb = findAll();
    final List<DistancePrice> pricesFromExcelFile = readDataFromExcel(file);
    final List<DistancePrice> pricesFromDbAndExcelFile = new ArrayList<>();
    pricesFromDbAndExcelFile.addAll(pricesFromDb);
    pricesFromDbAndExcelFile.addAll(pricesFromExcelFile);

    for (DistancePrice checkedPrice : pricesFromDbAndExcelFile) {

      for (DistancePrice price : pricesFromDbAndExcelFile) {

        if (checkedPrice == price) {
          continue;
        }

        if (!distancePriceChecker.isPriceUnique(checkedPrice, price)) {
          isUnique = false;
          throw new OverlappingDatesException(String.format("Distance price for a given work code (%s), machine (%s) and price type (%s) cannot overlap in time with the same entry",
              checkedPrice.getWorkCode().toString(), checkedPrice.getMachine().getInternalId(), checkedPrice.getPriceType().toString()));
        }

      }
    }

    return isUnique;
  }

  public DistancePrice update(Long id, DistancePrice editedDistancePrice) {

    Optional<DistancePrice> dbPrice = distancePriceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Distance price with id \'%s\' doesn`t exist.", id));
    }


//    if (!isOnlyPriceValueDifferent(dbPrice.get(), editedDistancePrice)) {
//      distancePriceChecker.checkPriceUsage(id);
//    }

    validateDeliveryPriceConsistency(id, editedDistancePrice);

    distancePriceChecker.checkEditability(id, dbPrice.get(), editedDistancePrice);

    editedDistancePrice.setId(id);
    return distancePriceRepository.save(editedDistancePrice);
  }

  private boolean isOnlyPriceValueDifferent(DistancePrice dbPrice, DistancePrice editedPrice) {
    return dbPrice.getWorkCode() == editedPrice.getWorkCode()
        && dbPrice.getMachine().getInternalId().equals(editedPrice.getMachine().getInternalId())
        && dbPrice.getPriceType() == editedPrice.getPriceType()
        && dbPrice.getRangeMin() == editedPrice.getRangeMin()
        && dbPrice.getRangeMax() == editedPrice.getRangeMax()
        && dbPrice.getStartDate().isEqual(editedPrice.getStartDate())
        && dbPrice.getEndDate().isEqual(editedPrice.getEndDate())
        && dbPrice.getProjectCode().equals(editedPrice.getProjectCode())
        && !dbPrice.getPrice().equals(editedPrice.getPrice());
  }

  private void validateDeliveryPriceConsistency(Long id, DistancePrice distancePrice) {

    dateChecker.checkEndDateAfterStartDate(distancePrice);

    if (!isPriceUnique(id, distancePrice)) {
      throw new OverlappingDatesException(String.format("Distance price for a given work code (%s), machine (%s) and price type (%s) cannot overlap in time with the same entry.",
          distancePrice.getWorkCode().toString(), distancePrice.getMachine().getInternalId(), distancePrice.getPriceType().toString()));
    }
  }

  private boolean isPriceUnique(Long id, DistancePrice editedPrice) {
    boolean isUnique = true;
    final List<DistancePrice> pricesFromDb = findAll();

    for (DistancePrice dbPrice : pricesFromDb) {

      if (id == dbPrice.getId()) {
        continue;
      }

      if (!distancePriceChecker.isPriceUnique(editedPrice, dbPrice)) {
        isUnique = false;
        throw new OverlappingDatesException(
            String.format("Distance price for a given work code (%s), machine number (%s), price type (%s) cannot overlap in time with the same entry.",
                editedPrice.getWorkCode(), editedPrice.getMachine().getInternalId(), editedPrice.getPriceType().toString()));
      }

    }

    return isUnique;
  }

  public Page<DistancePrice> search(List<WorkCode> workCode, String machineInternalId, List<PriceType> priceType, Pageable pageable) {

    if (isEmpty(workCode)) {
      workCode = new ArrayList<>(EnumSet.allOf(WorkCode.class));
    }

    if (isEmpty(priceType)) {
      priceType = new ArrayList<>(EnumSet.allOf(PriceType.class));
    }

    return distancePriceRepository.findByWorkCodeInAndMachineInternalIdContainingAndPriceTypeIn(workCode, machineInternalId, priceType, pageable);
  }

  public List<DistancePrice> getMatchingPrices(String machineNumber, LocalDate date) {

    List<DistancePrice> result = new ArrayList<>();

    List<DistancePrice> distancePrices = distancePriceRepository.findByMachineInternalIdEquals(machineNumber);

    for (DistancePrice price : distancePrices) {
      if ((date.isAfter(price.getStartDate()) || date.isEqual(price.getStartDate()))
          && (date.isBefore(price.getEndDate()) || date.isEqual(price.getEndDate()))) {

        result.add(price);
      }
    }
    return result;
  }

  public boolean isPriceMatching(LocalDate date, DistancePrice newPrice, DistancePrice matchingPrice, String editedMachineNumber) {
    return newPrice.getWorkCode() == matchingPrice.getWorkCode()
        && editedMachineNumber.equals(matchingPrice.getMachine().getInternalId())
        && newPrice.getPriceType() == matchingPrice.getPriceType()
        && newPrice.getProjectCode().equals(matchingPrice.getProjectCode())
        && (date.isAfter(matchingPrice.getStartDate()) || date.isEqual(matchingPrice.getStartDate()))
        && (date.isBefore(matchingPrice.getEndDate()) || date.isEqual(matchingPrice.getEndDate()));
  }


  public void updateOnDoubleChange(Long id, DoubleDistancePrice doubleDistancePrice) {

    DistancePrice editedDistancePrice = doubleDistancePrice.getEditedDistancePrice();
    DistancePrice newDistancePrice = doubleDistancePrice.getNewDistancePrice();

    Optional<DistancePrice> dbPriceOptional = distancePriceRepository.findById(id);
    if (!dbPriceOptional.isPresent()) {
      throw new NotFoundException(String.format("Distance price with id \'%s\' doesn`t exist.", id));
    }

    dateChecker.checkEndDateAfterStartDate(editedDistancePrice);
    dateChecker.checkEndDateAfterStartDate(newDistancePrice);

    DistancePrice dbPrice = dbPriceOptional.get();

    if (dateChecker.areSameDates(editedDistancePrice, dbPrice) && distancePriceChecker.areSameDistanceRanges(editedDistancePrice, dbPrice)) {
      throw new NothingChangedException("Dates or distance ranges haven`t been changed. Nothing has been updated.");
    }

    if (dateChecker.areDatesOverlapping(newDistancePrice, editedDistancePrice)) {
      throw new OverlappingDatesException("Dates cannot overlap in time.");
    }

//    if (distancePriceChecker.areDistanceRangesOverlapping(newDistancePrice, editedDistancePrice)) {
//      throw new OverlappingDistanceRangesException("Distance ranges cannot overlap with each other.");
//    }

    List<RoadCardEntry> roadCardEntries = roadCardEntryService.getWorkReportEntriesByDistancePrice(id);
    String newDistancePriceMachineInternalId = newDistancePrice.getMachine().getInternalId();
    List<DistancePrice> allDistancePricesByMachineInternalId = distancePriceRepository.findAllByMachineInternalId(newDistancePriceMachineInternalId);

    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      WorkDocument workDocument = roadCardEntry.getWorkDocument();
      LocalDate date = workDocument.getDate();

      double distance = roadCardEntry.getDistance();


      if (dateChecker.isDateMatching(date, editedDistancePrice) && distancePriceChecker.isDistanceMatching(distance, editedDistancePrice)) {

        for (DistancePrice dbDistancePrice : allDistancePricesByMachineInternalId) {

          if (dbPrice == dbDistancePrice) {
            continue;
          }

          if (!distancePriceChecker.isPriceUnique(editedDistancePrice, dbDistancePrice)) {
            throw new OverlappingDatesException("Edited distance price cannot overlap in time with the same entry.");
          }
        }
      } else if (dateChecker.isDateMatching(date, newDistancePrice) && distancePriceChecker.isDistanceMatching(distance, newDistancePrice)) {

        for (DistancePrice dbDistancePrice : allDistancePricesByMachineInternalId) {

          if (dbPrice == dbDistancePrice) {
            continue;
          }

          if (!distancePriceChecker.isPriceUnique(newDistancePrice, dbDistancePrice)) {
            throw new OverlappingDatesException("New distance price cannot overlap in time with the same entry.");
          }
        }
      } else {
        throw new NotFoundException(String.format("There is no distance price matching work document: %s entry dated at %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
            workDocument.getId(),
            date,
            roadCardEntry.getWorkCode(),
            workDocument.getMachine().getInternalId(),
            roadCardEntry.getStartHour(),
            roadCardEntry.getEndHour(),
            roadCardEntry.getLoadingPlace(),
            roadCardEntry.getMaterial().getType(),
            roadCardEntry.getUnloadingPlace(),
            roadCardEntry.getQuantity(),
            roadCardEntry.getMeasureUnit(),
            roadCardEntry.getDistance(),
            roadCardEntry.getCostCode().getFullCode(),
            roadCardEntry.getAcceptingPerson().getName()));
      }
    }


    WorkCode newDistancePriceWorkCode = newDistancePrice.getWorkCode();
    PriceType newDistancePricePriceType = newDistancePrice.getPriceType();
    BigDecimal newDistancePricePrice = newDistancePrice.getPrice();
    double newDistancePriceRangeMin = newDistancePrice.getRangeMin();
    double newDistancePriceRangeMax = newDistancePrice.getRangeMax();
    LocalDate newDistancePriceStartDate = newDistancePrice.getStartDate();
    LocalDate newDistancePriceEndDate = newDistancePrice.getEndDate();
    String newDistancePriceProjectCode = newDistancePrice.getProjectCode();

    distancePriceRepository.save(newDistancePrice);

    DistancePrice newDistancePriceFromDb = distancePriceRepository
        .findByWorkCodeInAndMachineInternalIdAndPriceTypeInAndPriceAndRangeMinAndRangeMaxAndStartDateAndEndDateAndProjectCode(
            newDistancePriceWorkCode, newDistancePriceMachineInternalId, newDistancePricePriceType, newDistancePricePrice, newDistancePriceRangeMin, newDistancePriceRangeMax, newDistancePriceStartDate, newDistancePriceEndDate, newDistancePriceProjectCode
        );

    editedDistancePrice.setId(id);
    distancePriceRepository.save(editedDistancePrice);
    Optional<DistancePrice> editedDistancePriceFromDb = distancePriceRepository.findById(id);


    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      WorkDocument workDocument = roadCardEntry.getWorkDocument();
      LocalDate date = workDocument.getDate();

      double distance = roadCardEntry.getDistance();


      if (dateChecker.isDateMatching(date, editedDistancePrice) && distancePriceChecker.isDistanceMatching(distance, editedDistancePrice)) {

        roadCardEntry.setDistancePrice(editedDistancePriceFromDb.get());
        roadCardEntryRepository.save(roadCardEntry);
      } else if (dateChecker.isDateMatching(date, newDistancePrice) && distancePriceChecker.isDistanceMatching(distance, newDistancePrice)) {

        roadCardEntry.setDistancePrice(newDistancePriceFromDb);
        roadCardEntryRepository.save(roadCardEntry);
      } else {
        return;
      }
    }


  }

  private boolean areSameDates(DistancePrice editedDistancePrice, DistancePrice dbPrice) {
    return dbPrice.getStartDate().isEqual(editedDistancePrice.getStartDate()) && dbPrice.getEndDate().isEqual(editedDistancePrice.getEndDate());
  }
}


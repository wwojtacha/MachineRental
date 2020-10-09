package machineRental.MR.price.hour.service;

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
import machineRental.MR.price.hour.HourPriceChecker;
import machineRental.MR.price.distance.service.DateChecker;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.hour.exception.IncorrectDateException;
import machineRental.MR.price.hour.exception.NothingChangedException;
import machineRental.MR.price.hour.exception.OverlappingDatesException;
import machineRental.MR.price.hour.model.DoubleHourPrice;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.repository.CostCodeRepository;
import machineRental.MR.repository.HourPriceRepository;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.WorkReportEntryRepository;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
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
public class HourPriceService {

  @Autowired
  private HourPriceRepository hourPriceRepository;

  @Autowired
  private MachineRepository machineRepository;

  @Autowired
  private ExcelHelper excelHelper;

  @Autowired
  private HourPriceChecker hourPriceChecker;

  @Autowired
  private WorkReportEntryService workReportEntryService;

  @Autowired
  private WorkReportEntryRepository workReportEntryRepository;

  @Autowired
  private CostCodeRepository costCodeRepository;

  private DateChecker dateChecker = new DateChecker();

  public List<HourPrice> findAll() {
    return (List<HourPrice>) hourPriceRepository.findAll();
  }

  public void saveDataFromExcelFile(MultipartFile file) {

    List<HourPrice> prices = readDataFromExcel(file);

    if (isPriceUnique(file)) {
      for (HourPrice price : prices) {
//        createPriceId(price);
        hourPriceRepository.save(price);
      }
    }
  }

  public List<HourPrice> readDataFromExcel(MultipartFile file) {

    final List<HourPrice> hourPricesFromExcelFile = new ArrayList<>();

    if (excelHelper.isProperFileType(file)) {
      Workbook workbook = excelHelper.getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        HourPrice hourPrice = new HourPrice();
        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
          String workCode = row.getCell(0).getStringCellValue();

          if (!EnumUtils.isValidEnum(WorkCode.class, workCode)) {
            throw new NotFoundException(String.format("Work code \'%s\' does not exist.", workCode));
          }
          hourPrice.setWorkCode(Enum.valueOf(WorkCode.class, workCode));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'work code\'. It must be a valid work code (eg. PS).");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String machineInternalId = String.valueOf(row.getCell(1));
          if (!machineRepository.existsByInternalId(machineInternalId)) {
            throw new NotFoundException(String.format("Machine with internal ID \'%s\' does not exist.", machineInternalId));
          }

          Machine machine = machineRepository.findByInternalId(machineInternalId);
          hourPrice.setMachine(machine);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'machineInternalId\'. It must be a string (text)!");
        }

        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING) {
          String priceType = String.valueOf(row.getCell(2));

          if (!EnumUtils.isValidEnum(PriceType.class, priceType)) {
            throw new NotFoundException(String.format("Price type \'%s\' does not exist.", priceType));
          }
          hourPrice.setPriceType(Enum.valueOf(PriceType.class, priceType));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'priceType\'. It must be a number!");
        }

        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(3).getNumericCellValue();
          BigDecimal price = BigDecimal.valueOf(cellDoubleValue);
          hourPrice.setPrice(price);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'price\'. It must be a number!");
        }

        LocalDate startDate;
        if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(4).getDateCellValue();
          startDate = convertToLocalDateViaInstant(cellDateValue);
          hourPrice.setStartDate(startDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'startDate\'. It must be a Date!");
        }

        if (row.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          Date cellDateValue = row.getCell(5).getDateCellValue();
          LocalDate endDate = convertToLocalDateViaInstant(cellDateValue);

          if (endDate.isBefore(startDate)) {
            throw new IncorrectDateException("End date must be equal or greater than start date.");
          }

          hourPrice.setEndDate(endDate);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'endDate\'. It must be a Date!");
        }

        if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
          String projectCode = row.getCell(6).getStringCellValue();

          if (!costCodeRepository.existsByProjectCode(projectCode)) {
            throw new NotFoundException(String.format("Project code \'%s\' does not exist.", projectCode));
          }

          hourPrice.setProjectCode(projectCode);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'project code\'. It must be a string (text).");
        }

//        createPriceId(hourPrice);
        hourPricesFromExcelFile.add(hourPrice);
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
    final List<HourPrice> pricesFromDb = findAll();
    final List<HourPrice> pricesFromExcelFile = readDataFromExcel(file);
    final List<HourPrice> pricesFromDbAndExcelFile = new ArrayList<>();
    pricesFromDbAndExcelFile.addAll(pricesFromDb);
    pricesFromDbAndExcelFile.addAll(pricesFromExcelFile);

    for (HourPrice checkedPrice : pricesFromDbAndExcelFile) {

      for (HourPrice price : pricesFromDbAndExcelFile) {

        if (checkedPrice == price) {
          continue;
        }

        if (!hourPriceChecker.isPriceUnique(checkedPrice, price)) {
          isUnique = false;
          throw new OverlappingDatesException(String.format("Hour price for a given work code (%s), machine (%s) and price type (%s) cannot overlap in time with the same entry",
              checkedPrice.getWorkCode().toString(), checkedPrice.getMachine().getInternalId(), checkedPrice.getPriceType().toString()));
        }
      }
    }

    return isUnique;
  }

  public HourPrice update(Long id, HourPrice editedHourPrice) {

    Optional<HourPrice> dbPrice = hourPriceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Hour price with id \'%s\' doesn`t exist.", id));
    }

    validateDeliveryPriceConsistency(id, editedHourPrice);

//    if (!isOnlyPriceValueDifferent(dbPrice.get(), editedHourPrice)) {
//      hourPriceChecker.checkPriceUsage(id);
//    }

    hourPriceChecker.checkEditability(id, dbPrice.get(), editedHourPrice);

    editedHourPrice.setId(id);
    return hourPriceRepository.save(editedHourPrice);
  }

  private boolean isOnlyPriceValueDifferent(HourPrice dbPrice, HourPrice editedPrice) {
    return dbPrice.getWorkCode() == editedPrice.getWorkCode()
        && dbPrice.getMachine().getInternalId().equals(editedPrice.getMachine().getInternalId())
        && dbPrice.getPriceType() == editedPrice.getPriceType()
        && dbPrice.getStartDate().isEqual(editedPrice.getStartDate())
        && dbPrice.getEndDate().isEqual(editedPrice.getEndDate())
        && dbPrice.getProjectCode().equals(editedPrice.getProjectCode())
        && !dbPrice.getPrice().equals(editedPrice.getPrice());
  }

  private void validateDeliveryPriceConsistency(Long id, HourPrice hourPrice) {

    dateChecker.checkEndDateAfterStartDate(hourPrice);

    if (!isPriceUnique(id, hourPrice)) {
      throw new OverlappingDatesException(String.format("Hour price for a given work code (%s), machine (%s) and price type (%s) cannot overlap in time with the same entry.",
          hourPrice.getWorkCode().toString(), hourPrice.getMachine().getInternalId(), hourPrice.getPriceType().toString()));
    }
  }

  private boolean isPriceUnique(Long id, HourPrice editedPrice) {
    boolean isUnique = true;
    final List<HourPrice> pricesFromDb = findAll();

    for (HourPrice dbPrice : pricesFromDb) {

      if (id == dbPrice.getId()) {
        continue;
      }

      if (!hourPriceChecker.isPriceUnique(editedPrice, dbPrice)) {
        isUnique = false;
        throw new OverlappingDatesException(
            String.format("Hour price for a given work code (%s), machine number (%s), price type (%s) cannot overlap in time with the same entry.",
                editedPrice.getWorkCode(), editedPrice.getMachine().getInternalId(), editedPrice.getPriceType().toString()));
      }

    }

    return isUnique;
  }

  public Page<HourPrice> search(List<WorkCode> workCode, String machineInternalId, List<PriceType> priceType, Pageable pageable) {

    if (isEmpty(workCode)) {
      workCode = new ArrayList<>(EnumSet.allOf(WorkCode.class));
    }

    if (isEmpty(priceType)) {
      priceType = new ArrayList<>(EnumSet.allOf(PriceType.class));
    }

    return hourPriceRepository.findByWorkCodeInAndMachineInternalIdContainingAndPriceTypeIn(workCode, machineInternalId, priceType, pageable);
  }

  public List<HourPrice> getMatchingPrices(String machineNumber, LocalDate date) {
    List<HourPrice> result = new ArrayList<>();

    List<HourPrice> hourPrices = hourPriceRepository.findByMachineInternalIdEquals(machineNumber);

    for (HourPrice price : hourPrices) {
      if ((date.isAfter(price.getStartDate()) || date.isEqual(price.getStartDate()))
          && (date.isBefore(price.getEndDate()) || date.isEqual(price.getEndDate()))) {

       result.add(price);
      }
    }
    return result;
  }

  public boolean isPriceMatching(LocalDate date, HourPrice hourPrice, HourPrice matchingPrice, String editedMachineNumber) {
    return hourPrice.getWorkCode() == matchingPrice.getWorkCode()
        && editedMachineNumber.equals(matchingPrice.getMachine().getInternalId())
        && hourPrice.getPriceType() == matchingPrice.getPriceType()
        && hourPrice.getProjectCode().equals(matchingPrice.getProjectCode())
        && (date.isAfter(matchingPrice.getStartDate()) || date.isEqual(matchingPrice.getStartDate()))
        && (date.isBefore(matchingPrice.getEndDate()) || date.isEqual(matchingPrice.getEndDate()));
  }

  public void updateOnDoubleChange(Long id, DoubleHourPrice doubleHourPrice) {

    HourPrice editedHourPrice = doubleHourPrice.getEditedHourPrice();
    HourPrice newHourPrice = doubleHourPrice.getNewHourPrice();

    Optional<HourPrice> dbPriceOptional = hourPriceRepository.findById(id);
    if (!dbPriceOptional.isPresent()) {
      throw new NotFoundException(String.format("Hour price with id \'%s\' doesn`t exist.", id));
    }

    dateChecker.checkEndDateAfterStartDate(editedHourPrice);
    dateChecker.checkEndDateAfterStartDate(newHourPrice);

    HourPrice dbPrice = dbPriceOptional.get();

    if (dateChecker.areSameDates(editedHourPrice, dbPrice)) {
      throw new NothingChangedException("Dates haven`t been changed. Nothing has been updated.");
    }

    if (dateChecker.areDatesOverlapping(newHourPrice, editedHourPrice)) {
      throw new OverlappingDatesException("Dates cannot overlap in time.");
    }

    List<WorkReportEntry> workReportEntries = workReportEntryService.getWorkReportEntriesByHourPrice(id);
    String newHourPriceMachineInternalId = newHourPrice.getMachine().getInternalId();
    List<HourPrice> allHourPricesByMachineInternalId = hourPriceRepository.findByMachineInternalIdEquals(newHourPriceMachineInternalId);

    for (WorkReportEntry workReportEntry : workReportEntries) {
      WorkDocument workDocument = workReportEntry.getWorkDocument();
      LocalDate date = workDocument.getDate();

      if (dateChecker.isDateMatching(date, editedHourPrice)) {
        for (HourPrice dbHourPrice : allHourPricesByMachineInternalId) {

          if (dbPrice == dbHourPrice) {
            continue;
          }

          if (!hourPriceChecker.isPriceUnique(editedHourPrice, dbHourPrice)) {
            throw new OverlappingDatesException("Edited hour price cannot overlap in time with the same entry.");
          }
        }
      } else if (dateChecker.isDateMatching(date, newHourPrice)) {


        for (HourPrice dbHourPrice : allHourPricesByMachineInternalId) {

          if (dbPrice == dbHourPrice) {
            continue;
          }

          if (!hourPriceChecker.isPriceUnique(newHourPrice, dbHourPrice)) {
            throw new OverlappingDatesException("New hour price cannot overlap in time with the same entry.");
          }
        }
      } else {
        throw new NotFoundException(String.format("There is no hour price matching work document: %s entry dated at %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
            workDocument.getId(),
            date,
            workReportEntry.getWorkCode(),
            workDocument.getMachine().getInternalId(),
            workReportEntry.getStartHour(),
            workReportEntry.getEndHour(),
            workReportEntry.getPlaceOfWork(),
            workReportEntry.getTypeOfWork(),
            workReportEntry.getWorkQuantity(),
            workReportEntry.getCostCode().getFullCode(),
            workReportEntry.getAcceptingPerson().getName()));
      }
    }


    WorkCode newHourPriceWorkCode = newHourPrice.getWorkCode();
    PriceType newHourPricePriceType = newHourPrice.getPriceType();
    BigDecimal newHourPricePrice = newHourPrice.getPrice();
    LocalDate newHourPriceStartDate = newHourPrice.getStartDate();
    LocalDate newHourPriceEndDate = newHourPrice.getEndDate();
    String newHourPriceProjectCode = newHourPrice.getProjectCode();


    hourPriceRepository.save(newHourPrice);

    HourPrice newHourPriceFromDb = hourPriceRepository
        .findByWorkCodeInAndMachineInternalIdAndPriceTypeInAndPriceAndStartDateAndEndDateAndProjectCode(
            newHourPriceWorkCode, newHourPriceMachineInternalId, newHourPricePriceType, newHourPricePrice, newHourPriceStartDate, newHourPriceEndDate, newHourPriceProjectCode
        );


    editedHourPrice.setId(id);
    hourPriceRepository.save(editedHourPrice);
    Optional<HourPrice> editedHourPriceFromDb = hourPriceRepository.findById(id);

    for (WorkReportEntry workReportEntry : workReportEntries) {
      WorkDocument workDocument = workReportEntry.getWorkDocument();
      LocalDate date = workDocument.getDate();

      if (dateChecker.isDateMatching(date, editedHourPrice)) {

        workReportEntry.setHourPrice(editedHourPriceFromDb.get());
        workReportEntryRepository.save(workReportEntry);
      } else if (dateChecker.isDateMatching(date, newHourPrice)) {

        workReportEntry.setHourPrice(newHourPriceFromDb);
        workReportEntryRepository.save(workReportEntry);
      } else {
        return;
      }
    }


  }

  public List<HourPrice> getHourPricesByProjectCode(String projectCode) {
    return hourPriceRepository.findByProjectCode(projectCode);
  }

}


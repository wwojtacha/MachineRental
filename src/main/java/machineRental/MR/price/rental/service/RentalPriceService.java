package machineRental.MR.price.rental.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import machineRental.MR.excel.ExcelHelper;
import machineRental.MR.price.rental.exception.NotUniquePriceYearAndMachineId;
import machineRental.MR.excel.WrongDataTypeException;
import org.springframework.data.domain.Pageable;
import machineRental.MR.exception.*;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.order.model.Order;
import machineRental.MR.price.rental.model.RentalPrice;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OrderRepository;
import machineRental.MR.repository.RentalPriceRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalPriceService {

  @Autowired
  private RentalPriceRepository rentalPriceRepository;

  @Autowired
  private MachineRepository machineRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ExcelHelper excelHelper;

  public List<RentalPrice> findAll() {
    return (List<RentalPrice>) rentalPriceRepository.findAll();
  }

  public List<RentalPrice> readDataFromExcel(MultipartFile file) {

    final List<RentalPrice> pricesFromExcelFile = new ArrayList<>();

    if (excelHelper.isProperFileType(file)) {
      Workbook workbook = excelHelper.getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        RentalPrice rentalPrice = new RentalPrice();
        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC && row.getCell(0).getNumericCellValue() > 1900 && row.getCell(0).getNumericCellValue() < 2100) {
          double cellDoubleValue = row.getCell(0).getNumericCellValue();
          String cellStringFromDouble = String.valueOf(cellDoubleValue);
          String properStringFormat = cellStringFromDouble.substring(0, cellStringFromDouble.length() - 2);
          Integer cellInteger = Integer.parseInt(properStringFormat);
          rentalPrice.setYear(cellInteger);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'year\'. It must be a number between 1900 and 2100.");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String cellValue = String.valueOf(row.getCell(1));
          rentalPrice.setPriceType(cellValue);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'priceType\'. It must be a string (text)!");
        }

        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(2).getNumericCellValue();
          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
          rentalPrice.setPrice(cellBigDecimal);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'price\'. It must be a number!");
        }

//        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//          double cellDoubleValue = row.getCell(2).getNumericCellValue();
//          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
//          sellPrice.setPriceFor7Days(cellBigDecimal);
//        } else {
//          throw new WrongDataTypeException("Wrong data type in column \'priceFor7Days\'. It must be a number!");
//        }

//        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//          double cellDoubleValue = row.getCell(3).getNumericCellValue();
//          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
//          sellPrice.setPriceFor30Days(cellBigDecimal);
//        } else {
//          throw new WrongDataTypeException("Wrong data type in column \'priceFor30Days\'. It must be a number!");
//        }

        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(3).getNumericCellValue();
          String cellString = String.valueOf(cellDoubleValue);
          String properStringFormat = cellString.substring(0, cellString.length() - 2);
          Long cellLong = Long.valueOf(properStringFormat);
          if (isMachinePresentInDb(cellLong)) {
            Machine machine = new Machine();
            machine.setId(cellLong);
            rentalPrice.setMachine(machine);
          } else {
            throw new NotFoundException(String.format("Machine with id \'%s\' doesn`t exist. File not uploaded to data base.", cellLong));
          }
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'machineId\'. It must be a number!");
        }

        createPriceId(rentalPrice);
        pricesFromExcelFile.add(rentalPrice);
      }
    }
    return pricesFromExcelFile;
  }

  private boolean isMachinePresentInDb(Long machineId) {
    boolean isMachineInDb = false;
    Optional<Machine> dbMachine = machineRepository.findById(machineId);

    if (dbMachine.isPresent()) {
      isMachineInDb = true;
    } else {
      isMachineInDb = false;
    }
    return isMachineInDb;
  }

  private boolean isPriceUnique(MultipartFile file) {

    boolean isUnique = false;
    final List<RentalPrice> pricesFromDb = findAll();
    final List<RentalPrice> pricesFromExcelFile = readDataFromExcel(file);
    final List<RentalPrice> pricesFromDbAndExcelFile = new ArrayList<>();
    pricesFromDbAndExcelFile.addAll(pricesFromDb);
    pricesFromDbAndExcelFile.addAll(pricesFromExcelFile);

    List<String> pricesId = new ArrayList<>();

    for (RentalPrice rentalPrice : pricesFromDbAndExcelFile) {
//      Integer year = sellPrice.getYear();
//      Long machineId = sellPrice.getMachine().getId();
//      String yearString = String.valueOf(year);
//      String machineIdString = String.valueOf(machineId);
//      String priceId = yearString + machineIdString;
      String priceId = rentalPrice.getId();
      pricesId.add(priceId);
    }

    Set<String> uniquePrices = new HashSet<>();

    for (String priceId : pricesId) {
      if (!uniquePrices.add(priceId)) {
//        String[] priceIdComponents = priceId.split(PRICE_ID_SEPARATOR);
//        String priceYear = priceIdComponents[0];
//        String machineId = priceIdComponents[1];
//        String priceYear = priceId.substring(0, 4);
//        String machineId = priceId.substring(4);

        isUnique = false;
        throw new NotUniquePriceYearAndMachineId(String.format("Only one sellPrice entity allowed for a given machine in a single year for given sellPrice type." +
              " Price with id \'%s\' either already exists in data base or is present multiple times in Excel file", priceId));
      } else {
        isUnique = true;
      }
    }

    return isUnique;
  }

  public void saveDataFromExcelFile(MultipartFile file) {

    List<RentalPrice> rentalPrices = readDataFromExcel(file);

    if (isPriceUnique(file)) {
      for (RentalPrice rentalPrice : rentalPrices) {
        createPriceId(rentalPrice);
        rentalPriceRepository.save(rentalPrice);
      }
    }
  }

  public RentalPrice update(String id, RentalPrice rentalPrice, BindingResult bindingResult) {

    Optional<RentalPrice> dbPrice = rentalPriceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id \'%s\' doesn`t exist.", id));
    }

    if (isPriceUsedInOrder(rentalPrice)) {
      bindingResult.addError(new FieldError("sellPrice", "id", String.format("Price with id \'%s\' is already used in at least one order thus cannot be edited.", id)));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }

    Machine machine = rentalPrice.getMachine();
    Long machineId = machine.getId();
    String machineInternalId = machine.getInternalId();


    if (machineId == null) {
      Machine dbMachine = machineRepository.findByInternalId(machineInternalId);
      rentalPrice.setMachine(dbMachine);
    }

    rentalPrice.setId(id);
    return rentalPriceRepository.save(rentalPrice);
  }

  private boolean isPriceUsedInOrder(RentalPrice rentalPrice) {
    Integer year = rentalPrice.getYear();
    LocalDate localDate = LocalDate.of(year, 1, 1);
    List<Order> orders = orderRepository.findByStartDateAfterAndMachine_InternalIdAndDbPriceTrue(localDate, rentalPrice.getMachine().getInternalId());
    return !orders.isEmpty();
  }

//  public void delete(String id, BindingResult bindingResult) {
//    Optional<Price> dbPrice = priceRepository.findById(id);
//    if (!dbPrice.isPresent()) {
//      throw new NotFoundException(String.format("Price with id \'%s\' doesn`t exist.", id));
//    }
//
//    if (isPriceUsedInOrder(dbPrice.get())) {
//      bindingResult.addError(
//          new FieldError("sellPrice", "machineInternalId", String.format("Price for machine \'%s\' in year \'%d\' is already used in orders thus cannot be deleted. Remove required order(s) first.",
//              dbPrice.get().getMachine().getInternalId(), dbPrice.get().getYear())));
//    }
//
//    if (bindingResult.hasErrors()) {
//      throw new BindingResultException(bindingResult);
//    }
//    priceRepository.deleteById(id);
//  }

  public void delete(String id) {
    Optional<RentalPrice> dbPrice = rentalPriceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id \'%s\' doesn`t exist.", id));
    }

    if (isPriceUsedInOrder(dbPrice.get())) {
      throw new AlreadyUsedException(String.format("Price with id: \'%s\' is already used in at least in one order thus can not be deleted.", id));
    }
    rentalPriceRepository.deleteById(id);
  }

  public Page<RentalPrice> search(Integer year, String machineInternalId, String priceType, Pageable pageable) {
    if (year == null) {
      return rentalPriceRepository.findByMachine_InternalIdContainingAndPriceTypeContaining(machineInternalId, priceType, pageable);
    } else {
      return rentalPriceRepository.findByYearEqualsAndMachine_InternalIdContainingAndPriceTypeContaining(year, machineInternalId, priceType, pageable);
    }
  }

  public RentalPrice getById(String id) {
    Optional<RentalPrice> dbPrice = rentalPriceRepository.findById(id);

    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id: \'%s\' does not exist.", id));
    }

    return dbPrice.get();
  }

  private void createPriceId(RentalPrice rentalPrice) {
    Integer year = rentalPrice.getYear();
    String yearString = String.valueOf(year);

    Long machineId = rentalPrice.getMachine().getId();
    String machineIdString = String.valueOf(machineId);

    String priceType = rentalPrice.getPriceType();

    BigDecimal priceValue = rentalPrice.getPrice().setScale(2);
    String priceValueString = String.valueOf(priceValue);

    String priceId = yearString + machineIdString + priceType + priceValueString;
    rentalPrice.setId(priceId);
  }

  public boolean isMachineUsed(Long machineId) {
    return rentalPriceRepository.existsByMachine_Id(machineId);
  }

}


package machineRental.MR.price.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import machineRental.MR.price.exception.NotUniquePriceYearAndMachineId;
import machineRental.MR.price.exception.UsedPriceException;
import machineRental.MR.price.exception.WrongDataTypeException;
import machineRental.MR.price.exception.WrongFileTypeException;
import org.springframework.data.domain.Pageable;
import machineRental.MR.exception.*;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.order.model.Order;
import machineRental.MR.price.model.Price;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OrderRepository;
import machineRental.MR.repository.PriceRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PriceService {

  @Autowired
  private PriceRepository priceRepository;

  @Autowired
  private MachineRepository machineRepository;

  @Autowired
  private OrderRepository orderRepository;

  public List<Price> findAll() {
    return (List<Price>) priceRepository.findAll();
  }

  private boolean isProperFileType(MultipartFile file) {
    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
    boolean isProperFile = false;
    if ("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)) {
      isProperFile = true;
    } else {
      isProperFile = false;
      throw new WrongFileTypeException("Only Excel files can be uploaded!");
    }
    return isProperFile;
  }

  public List<Price> readDataFromExcel(MultipartFile file) {

    final List<Price> pricesFromExcelFile = new ArrayList<>();

    if (isProperFileType(file)) {
      Workbook workbook = getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        Price price = new Price();
        if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC && row.getCell(0).getNumericCellValue() > 1900 && row.getCell(0).getNumericCellValue() < 2100) {
          double cellDoubleValue = row.getCell(0).getNumericCellValue();
          String cellStringFromDouble = String.valueOf(cellDoubleValue);
          String properStringFormat = cellStringFromDouble.substring(0, cellStringFromDouble.length() - 2);
          Integer cellInteger = Integer.parseInt(properStringFormat);
          price.setYear(cellInteger);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'year\'. It must be a number between 1900 and 2100.");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String cellValue = String.valueOf(row.getCell(1));
          price.setPriceType(cellValue);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'priceType\'. It must be a string (text)!");
        }

        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(2).getNumericCellValue();
          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
          price.setPrice(cellBigDecimal);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'price\'. It must be a number!");
        }

//        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//          double cellDoubleValue = row.getCell(2).getNumericCellValue();
//          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
//          price.setPriceFor7Days(cellBigDecimal);
//        } else {
//          throw new WrongDataTypeException("Wrong data type in column \'priceFor7Days\'. It must be a number!");
//        }

//        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//          double cellDoubleValue = row.getCell(3).getNumericCellValue();
//          BigDecimal cellBigDecimal = BigDecimal.valueOf(cellDoubleValue);
//          price.setPriceFor30Days(cellBigDecimal);
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
            price.setMachine(machine);
          } else {
            throw new NotFoundException(String.format("Machine with id \'%s\' doesn`t exist. File not uploaded to data base.", cellLong));
          }
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'machineId\'. It must be a number!");
        }

        createPriceId(price);
        pricesFromExcelFile.add(price);
      }
    }
    return pricesFromExcelFile;
  }

  private Workbook getWorkBook(MultipartFile file) {
    Workbook workbook = null;
    String extension = FilenameUtils.getExtension(file.getOriginalFilename());

    try {
      if ("xlsx".equalsIgnoreCase(extension)) {
        workbook = new XSSFWorkbook(file.getInputStream());
      } else if ("xls".equalsIgnoreCase(extension)) {
        workbook = new HSSFWorkbook(file.getInputStream());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return workbook;
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
    final List<Price> pricesFromDb = findAll();
    final List<Price> pricesFromExcelFile = readDataFromExcel(file);
    final List<Price> pricesFromDbAndExcelFile = new ArrayList<>();
    pricesFromDbAndExcelFile.addAll(pricesFromDb);
    pricesFromDbAndExcelFile.addAll(pricesFromExcelFile);

    List<String> pricesId = new ArrayList<>();

    for (Price price : pricesFromDbAndExcelFile) {
//      Integer year = price.getYear();
//      Long machineId = price.getMachine().getId();
//      String yearString = String.valueOf(year);
//      String machineIdString = String.valueOf(machineId);
//      String priceId = yearString + machineIdString;
      String priceId = price.getId();
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
        throw new NotUniquePriceYearAndMachineId(String.format("Only one price entity allowed for a given machine in a single year for given price type." +
              " Price with id \'%s\' either already exists in data base or is present multiple times in Excel file", priceId));
      } else {
        isUnique = true;
      }
    }

    return isUnique;
  }

  public void saveDataFromExcelFile(MultipartFile file) {

    List<Price> prices = readDataFromExcel(file);

    if (isPriceUnique(file)) {
      for (Price price : prices) {
        createPriceId(price);
        priceRepository.save(price);
      }
    }
  }

  public Price update(String id, Price price, BindingResult bindingResult) {

    Optional<Price> dbPrice = priceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id \'%s\' doesn`t exist.", id));
    }

    if (isPriceUsedInOrder(price)) {
      bindingResult.addError(new FieldError("price", "id", String.format("Price with id \'%s\' is already used in at least one order thus cannot be edited.", id)));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }

    Machine machine = price.getMachine();
    Long machineId = machine.getId();
    String machineInternalId = machine.getInternalId();


    if (machineId == null) {
      Machine dbMachine = machineRepository.findByInternalId(machineInternalId);
      price.setMachine(dbMachine);
    }

    price.setId(id);
    return priceRepository.save(price);
  }

  private boolean isPriceUsedInOrder(Price price) {
    Integer year = price.getYear();
    LocalDate localDate = LocalDate.of(year, 1, 1);
    List<Order> orders = orderRepository.findByStartDateAfterAndMachine_InternalIdAndDbPriceTrue(localDate, price.getMachine().getInternalId());
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
//          new FieldError("price", "machineInternalId", String.format("Price for machine \'%s\' in year \'%d\' is already used in orders thus cannot be deleted. Remove required order(s) first.",
//              dbPrice.get().getMachine().getInternalId(), dbPrice.get().getYear())));
//    }
//
//    if (bindingResult.hasErrors()) {
//      throw new BindingResultException(bindingResult);
//    }
//    priceRepository.deleteById(id);
//  }

  public void delete(String id) {
    Optional<Price> dbPrice = priceRepository.findById(id);
    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id \'%s\' doesn`t exist.", id));
    }

    if (isPriceUsedInOrder(dbPrice.get())) {
      throw new UsedPriceException(String.format("Price with id: \'%s\' is already used in at least in one order thus can not be deleted.", id));
    }
    priceRepository.deleteById(id);
  }

  public Page<Price> search(Integer year, String machineInternalId, String priceType, Pageable pageable) {
    if (year == null) {
      return priceRepository.findByMachine_InternalIdContainingAndPriceTypeContaining(machineInternalId, priceType, pageable);
    } else {
      return priceRepository.findByYearEqualsAndMachine_InternalIdContainingAndPriceTypeContaining(year, machineInternalId, priceType, pageable);
    }
  }

  public Price getById(String id) {
    Optional<Price> dbPrice = priceRepository.findById(id);

    if (!dbPrice.isPresent()) {
      throw new NotFoundException(String.format("Price with id: \'%s\' does not exist.", id));
    }

    return dbPrice.get();
  }

  private void createPriceId(Price price) {
    Integer year = price.getYear();
    String yearString = String.valueOf(year);

    Long machineId = price.getMachine().getId();
    String machineIdString = String.valueOf(machineId);

    String priceType = price.getPriceType();

    BigDecimal priceValue = price.getPrice().setScale(2);
    String priceValueString = String.valueOf(priceValue);

    String priceId = yearString + machineIdString + priceType + priceValueString;
    price.setId(priceId);
  }

}


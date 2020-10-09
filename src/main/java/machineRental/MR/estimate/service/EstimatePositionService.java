package machineRental.MR.estimate.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.costcode.service.CostCodeService;
import machineRental.MR.delivery.document.service.DeliveryDocumentService;
import machineRental.MR.delivery.entry.service.DeliveryDocumentEntryService;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.excel.AlreadyInDbException;
import machineRental.MR.excel.ExcelHelper;
import machineRental.MR.excel.NotPresentInDbException;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.excel.WrongDataTypeException;
import machineRental.MR.repository.EstimatePositionRepository;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EstimatePositionService {

  @Autowired
  private EstimatePositionRepository estimatePositionRepository;

  @Autowired
  private ExcelHelper excelHelper;

  @Autowired
  private CostCodeService costCodeService;

  @Autowired
  private WorkReportEntryService workReportEntryService;

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  @Autowired
  private DeliveryDocumentEntryService deliveryDocumentEntryService;


  public void saveDataFromExcel(MultipartFile file) {

    List<EstimatePosition> estimatePositions = readDataFromExcel(file);

      for (EstimatePosition estimatePosition : estimatePositions) {
        estimatePositionRepository.save(estimatePosition);
    }
  }

  private List<EstimatePosition> readDataFromExcel(MultipartFile file) {
    final List<EstimatePosition> estimatePositionsFromExcelFile = new ArrayList<>();

    if (excelHelper.isProperFileType(file)) {
      Workbook workbook = excelHelper.getWorkBook(file);
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      rows.next();
      while (rows.hasNext()) {
        Row row = rows.next();
        EstimatePosition estimatePosition = new EstimatePosition();
        String name = "";
        if (row.getCell(0) != null && Cell.CELL_TYPE_STRING == row.getCell(0).getCellType()) {
          name = String.valueOf(row.getCell(0));

          estimatePosition.setName(name);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'name\'. It must be a string (text)!");
        }

        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
          String cellValue = String.valueOf(row.getCell(1));

          if (!costCodeService.existsByFullCode(cellValue)) {
            throw new NotPresentInDbException(String.format("Cost code %s is not present in data base. Please correct or add to data base.", cellValue));
          }

          CostCode dbCostCode = costCodeService.getByFullCode(cellValue);
          String projectCode = dbCostCode.getProjectCode();

          if (estimatePositionRepository.existsByNameAndCostCode_ProjectCode(name, projectCode)) {
            throw new AlreadyInDbException(String.format("Estimate position with name and project code \'%s + %s\' already exists in data base. Name must be unique.", name, projectCode));
          }

          estimatePosition.setCostCode(dbCostCode);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'cost code\'. It must be a string (text)!");
        }

        double quantity = 0.0;
        if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          quantity = row.getCell(2).getNumericCellValue();
          estimatePosition.setQuantity(quantity);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'quantity\'. It must be a number!");
        }

        if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
          String cellValue = String.valueOf(row.getCell(3));
          estimatePosition.setMeasureUnit(cellValue);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'measure unit\'. It must be a string (text)!");
        }

        BigDecimal sellPrice = new BigDecimal(0);
        if (row.getCell(4).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(4).getNumericCellValue();
          sellPrice = BigDecimal.valueOf(cellDoubleValue);
          estimatePosition.setSellPrice(sellPrice);
          estimatePosition.setSellValue(sellPrice.multiply(BigDecimal.valueOf(quantity)));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'sellPrice\'. It must be a number!");
        }

        if (row.getCell(6).getCellType() == Cell.CELL_TYPE_STRING) {
          String cellValue = String.valueOf(row.getCell(6));
          estimatePosition.setRemarks(cellValue);
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'remarks\'. It must be a string (text)!");
        }

        BigDecimal costPrice = new BigDecimal(0);
        if (row.getCell(7).getCellType() == Cell.CELL_TYPE_NUMERIC) {
          double cellDoubleValue = row.getCell(7).getNumericCellValue();
          costPrice = BigDecimal.valueOf(cellDoubleValue);
          estimatePosition.setCostPrice(costPrice);
          estimatePosition.setCostValue(costPrice.multiply(BigDecimal.valueOf(quantity)));
        } else {
          throw new WrongDataTypeException("Wrong data type in column \'costPrice\'. It must be a number!");
        }

        estimatePositionsFromExcelFile.add(estimatePosition);
      }
    }
    return estimatePositionsFromExcelFile;
  }

  public Page<EstimatePosition> search(String name, String projectCode, String costType, String remarks, Pageable pageable) {
    return estimatePositionRepository.findByNameContainingAndCostCode_ProjectCodeContainingAndCostCode_CostTypeContainingAndRemarksContaining(name, projectCode, costType, remarks, pageable);
  }

  public EstimatePosition update(Long id, EstimatePosition editedEstimatePosition, BindingResult bindingResult) {
    Optional<EstimatePosition> dbEstimatePosition = estimatePositionRepository.findById(id);
    if(!dbEstimatePosition.isPresent()) {
      throw new NotFoundException("Estimate position: " + "\'" + id + "\'" + " does not exist");
    }

    validateEstimatePositionConsistency(editedEstimatePosition, dbEstimatePosition.get(), bindingResult);

    workReportEntryService.updateOnEstimatePositionChange(id, editedEstimatePosition);
    roadCardEntryService.updateOnEstimatePositionChange(id, editedEstimatePosition);
    deliveryDocumentEntryService.updateOnEstimatePositionChange(id, editedEstimatePosition);

    double quantity = editedEstimatePosition.getQuantity();
    BigDecimal sellPrice = editedEstimatePosition.getSellPrice();
    BigDecimal sellValue = sellPrice.multiply(BigDecimal.valueOf(quantity));
    editedEstimatePosition.setSellValue(sellValue);

    BigDecimal costPrice = editedEstimatePosition.getCostPrice();
    BigDecimal costValue = costPrice.multiply(BigDecimal.valueOf(quantity));
    editedEstimatePosition.setCostValue(costValue);

    editedEstimatePosition.setId(id);
    return estimatePositionRepository.save(editedEstimatePosition);
  }

  private void validateEstimatePositionConsistency(EstimatePosition estimatePosition, EstimatePosition currentEstimatePosition, BindingResult bindingResult) {

    String estimatePositionName = estimatePosition.getName();
    String estimatePositionProjectCode = estimatePosition.getCostCode().getProjectCode();
    String estimatePostitionId = estimatePositionName + estimatePositionProjectCode;

    String currentEstimatePositionName = currentEstimatePosition.getName();
    String currentEstimatePositionProjectCode = currentEstimatePosition.getCostCode().getProjectCode();
    String currentEstimatePostitionId = currentEstimatePositionName + currentEstimatePositionProjectCode;

    if(estimatePositionRepository.existsByNameAndCostCode_ProjectCode(estimatePositionName, estimatePositionProjectCode)
        && !estimatePostitionId.equals(currentEstimatePostitionId)) {
      bindingResult.addError(new FieldError(
          "estimatePosition",
          "estimatePosition",
          String.format("Estimate position: \'%s + %s\' already exists", estimatePositionName, estimatePositionProjectCode)
      ));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public List<EstimatePosition> getEstimatePositionsByCostCode(Long costCodeId) {
    return estimatePositionRepository.findByCostCode_Id(costCodeId);
  }
}

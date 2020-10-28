package machineRental.MR.dailyReport;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.repository.DailyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class DailyReportService {

  @Autowired
  private DailyReportRepository dailyReportRepository;

  public DailyReportDto create(DailyReportDto dailyReportDto, BindingResult bindingResult) {

    DailyReport dailyReport = convertToEntity(dailyReportDto);
    validateDailyReportConsistency(dailyReport, null, bindingResult);

    dailyReportRepository.save(dailyReport);

    return convertToDto(dailyReport);
  }

  private DailyReport convertToEntity(DailyReportDto dailyReportDto) {
    Long id = dailyReportDto.getId();
    LocalDate date = dailyReportDto.getDate();
    EstimatePosition estimatePosition = dailyReportDto.getEstimatePosition();
    String location = dailyReportDto.getLocation();
    String startPoint = dailyReportDto.getStartPoint();
    String endPoint = dailyReportDto.getEndPoint();
    String side = dailyReportDto.getSide();
    double quantity = dailyReportDto.getQuantity();
    String measureUnit = dailyReportDto.getMeasureUnit();
    String remarks = dailyReportDto.getRemarks();

    DailyReport dailyReport = new DailyReport(id, date, estimatePosition, location, startPoint, endPoint, side, quantity, measureUnit, remarks);

    return dailyReport;
  }

  private DailyReportDto convertToDto(DailyReport dailyReport) {

    Long id = dailyReport.getId();
    LocalDate date = dailyReport.getDate();
    EstimatePosition estimatePosition = dailyReport.getEstimatePosition();
    String location = dailyReport.getLocation();
    String startPoint = dailyReport.getStartPoint();
    String endPoint = dailyReport.getEndPoint();
    String side = dailyReport.getSide();
    double quantity = dailyReport.getQuantity();
    String measureUnit = dailyReport.getMeasureUnit();
    String remarks = dailyReport.getRemarks();

    DailyReportDto dailyReportDto = new DailyReportDto(id, date, estimatePosition, location, startPoint, endPoint, side, quantity, measureUnit, remarks);

    return dailyReportDto;
  }

  private void validateDailyReportConsistency(DailyReport editedDailyReport, DailyReport existingDailyReport, BindingResult bindingResult) {

    LocalDate editedDailyReportDate = editedDailyReport.getDate();
    EstimatePosition editedDailyReportEstimatePosition = editedDailyReport.getEstimatePosition();

    LocalDate existingDailyReportDate = LocalDate.MIN;
    EstimatePosition existingDailyReportEstimatePosition = new EstimatePosition();

    if (existingDailyReport != null) {
      existingDailyReportDate = existingDailyReport.getDate();
      existingDailyReportEstimatePosition = existingDailyReport.getEstimatePosition();
    }

    if(dailyReportRepository.existsByDateAndEstimatePosition(editedDailyReportDate, editedDailyReportEstimatePosition)
        && (!editedDailyReportDate.equals(existingDailyReportDate)
        || !editedDailyReportEstimatePosition.equals(existingDailyReportEstimatePosition))) {
      bindingResult.addError(new FieldError(
          "DailyReport",
          "dailyReportId",
          String.format("Daily report dated at %s with estimate position: \'%s;%s\' already exists",
              editedDailyReportDate,
              editedDailyReportEstimatePosition.getName(),
              editedDailyReportEstimatePosition.getCostCode().getFullCode())));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public DailyReportDto update(Long id, DailyReportDto dailyReportDto, BindingResult bindingResult) {
    Optional<DailyReport> dbDailyReport = dailyReportRepository.findById(id);
    if(!dbDailyReport.isPresent()) {
      LocalDate date = dailyReportDto.getDate();
      EstimatePosition estimatePosition = dailyReportDto.getEstimatePosition();
      throw new NotFoundException(String.format("Daily report dated at %s with estimate position \'%s;%s\' does not exist",
          date, estimatePosition.getName(), estimatePosition.getCostCode().getFullCode()));
    }

    DailyReport dailyReport = convertToEntity(dailyReportDto);
    validateDailyReportConsistency(dailyReport, dbDailyReport.get(), bindingResult);

    dailyReport.setId(id);
    dailyReportRepository.save(dailyReport);
    return convertToDto(dailyReport);
  }

  public void delete(Long id) {

    Optional<DailyReport> dbDailyReport = dailyReportRepository.findById(id);

    if (!dbDailyReport.isPresent()) {
      throw new NotFoundException(String.format("Daily report with id \'%s\' doesn`t exist!", id));
    }

    dailyReportRepository.deleteById(id);
  }

  public Page<DailyReportDto> search(LocalDate date, String estimatePositionName, String estimatePositionCostCode, String location, Pageable pageable) {

    Page<DailyReport> dailyReports;

    if (date == null) {
      dailyReports = dailyReportRepository.findByEstimatePosition_NameContainingAndEstimatePosition_CostCode_FullCodeContainingAndLocationContaining(estimatePositionName, estimatePositionCostCode, location, pageable);
    } else {
      dailyReports = dailyReportRepository.findByDateEqualsAndEstimatePosition_NameContainingAndEstimatePosition_CostCode_FullCodeContainingAndLocationContaining(date, estimatePositionName, estimatePositionCostCode, location, pageable);
    }

    return dailyReports.map(this::convertToDto);
  }

  public List<DailyReport> getDailyReportsForProjectCodeBetweenDates(LocalDate startDate, LocalDate endDate, String projectCode) {
    return dailyReportRepository.findByDateBetweenAndEstimatePosition_CostCode_ProjectCode(startDate, endDate, projectCode);
  }

  public Map<EstimatePosition, Double> getDailyReportQuantityPerEstimatePosition(LocalDate startDate, LocalDate endDate, String projectCode) {
    List<DailyReport> dailyReports = getDailyReportsForProjectCodeBetweenDates(startDate, endDate, projectCode);

    Map<EstimatePosition, Double> estimatePositionToTotalDailyReportQuantity = new HashMap<>();

    for (DailyReport dailyReport : dailyReports) {

      EstimatePosition estimatePosition = dailyReport.getEstimatePosition();

      double currentDailyReportQuantity = dailyReport.getQuantity();

      Double dailyReportQuantity = estimatePositionToTotalDailyReportQuantity.get(estimatePosition);

      if (dailyReportQuantity == null) {
        estimatePositionToTotalDailyReportQuantity.put(estimatePosition, currentDailyReportQuantity);
      } else {
        dailyReportQuantity += currentDailyReportQuantity;
        estimatePositionToTotalDailyReportQuantity.put(estimatePosition, dailyReportQuantity);
      }
    }

    return estimatePositionToTotalDailyReportQuantity;
  }
}

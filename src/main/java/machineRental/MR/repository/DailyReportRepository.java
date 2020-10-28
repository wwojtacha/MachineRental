package machineRental.MR.repository;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.dailyReport.DailyReport;
import machineRental.MR.estimate.model.EstimatePosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

  boolean existsByDateAndEstimatePosition(LocalDate date, EstimatePosition estimatePosition);

  Page<DailyReport> findByDateEqualsAndEstimatePosition_NameContainingAndEstimatePosition_CostCode_FullCodeContainingAndLocationContaining(LocalDate date, String estimatePositionName, String estimatePositionCostCode, String location, Pageable pageable);

  Page<DailyReport> findByEstimatePosition_NameContainingAndEstimatePosition_CostCode_FullCodeContainingAndLocationContaining(String estimatePositionName, String estimatePositionCostCode, String location, Pageable pageable);

  List<DailyReport> findByDateBetweenAndEstimatePosition_CostCode_ProjectCode(LocalDate startDate, LocalDate endDate, String projectCode);

}

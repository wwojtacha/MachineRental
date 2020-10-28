package machineRental.MR.repository;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.reports.cost.equipment.EquipmentCost;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkReportEntryRepository extends JpaRepository<WorkReportEntry, Long> {

  List<WorkReportEntry> findAllByWorkDocument_Id(String workDocumentId);

  void deleteByWorkDocument_Id(String workDocumentId);

  List<WorkReportEntry> findByWorkDocument_OperatorAndWorkDocument_DateEquals(Operator operator, LocalDate date);
  List<WorkReportEntry> findByWorkDocument_MachineAndWorkDocument_DateEquals(Machine machine, LocalDate date);


  boolean existsByHourPrice_Id(Long priceId);

  List<WorkReportEntry> findAllByHourPrice_Id(Long id);

  List<WorkReportEntry> findByEstimatePosition_CostCode_ProjectCode(String projectCode);

  List<WorkReportEntry> findByEstimatePosition_Id(Long estimateId);

  List<WorkReportEntry> findByWorkDocument_DateBetween(LocalDate startDate, LocalDate endDate);


  @Query(
      value = "SELECT machine_types.machine_type AS machineType, (work_reports_entries.end_hour - work_reports_entries.start_hour) AS workHoursCount, (hour_prices.price * workHoursCount) AS costValue",
      nativeQuery = true)
  List<EquipmentCost> getEquipmentCostByMachineType();

  List<WorkReportEntry> findByWorkDocument_DateBetweenAndEstimatePosition_CostCode_ProjectCodeEquals(LocalDate startDate, LocalDate endDate, String projectCode);


}

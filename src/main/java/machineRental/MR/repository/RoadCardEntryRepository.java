package machineRental.MR.repository;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadCardEntryRepository extends JpaRepository<RoadCardEntry, Long> {

  List<RoadCardEntry> findByWorkDocument_OperatorAndWorkDocument_DateEquals(Operator operator, LocalDate date);

  List<RoadCardEntry> findByWorkDocument_MachineAndWorkDocument_DateEquals(Machine machine, LocalDate date);

  List<RoadCardEntry> findAllByWorkDocument_Id(String workDocumentId);

  void deleteByWorkDocument_Id(String workDocumentId);

  boolean existsByDistancePrice_Id(Long priceId);

  List<RoadCardEntry> findAllByDistancePrice_Id(Long priceId);

  List<RoadCardEntry> findByEstimatePosition_CostCode_ProjectCode(String projectCode);

  List<RoadCardEntry> findByEstimatePosition_Id(Long estimateId);
}

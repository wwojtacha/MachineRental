package machineRental.MR.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import machineRental.MR.price.PriceType;
import machineRental.MR.price.hour.model.HourPrice;
import machineRental.MR.workDocumentEntry.WorkCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HourPriceRepository extends JpaRepository<HourPrice, Long> {

  Page<HourPrice> findByWorkCodeInAndMachineInternalIdContainingAndPriceTypeIn(List<WorkCode> workCode, String machineNumber, List<PriceType> priceType, Pageable pageable);

  List<HourPrice> findByMachineInternalIdEquals(String machineNumber);

  HourPrice findByWorkCodeInAndMachineInternalIdAndPriceTypeInAndPriceAndStartDateAndEndDateAndProjectCode(
      WorkCode workCode,
      String machineInternalId,
      PriceType priceType,
      BigDecimal Price,
      LocalDate startDate,
      LocalDate endDate,
      String projectCode);

  List<HourPrice> findByProjectCode(String projectCode);

  boolean existsByMachine_Id(Long machineId);
}

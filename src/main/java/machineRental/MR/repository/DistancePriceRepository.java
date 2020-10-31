package machineRental.MR.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import machineRental.MR.price.distance.model.DistancePrice;
import machineRental.MR.price.PriceType;
import machineRental.MR.workDocumentEntry.WorkCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistancePriceRepository extends JpaRepository<DistancePrice, Long> {

    Page<DistancePrice> findByWorkCodeInAndMachineInternalIdContainingAndPriceTypeIn(List<WorkCode> workCode, String machineNumber, List<PriceType> priceType, Pageable pageable);

    List<DistancePrice> findByMachineInternalIdEquals(String machineNumber);

    DistancePrice findByWorkCodeInAndMachineInternalIdAndPriceTypeInAndPriceAndRangeMinAndRangeMaxAndStartDateAndEndDateAndProjectCode(
        WorkCode workCode,
        String machineInternalId,
        PriceType priceType,
        BigDecimal price,
        double rangeMin,
        double rangeMax,
        LocalDate startDate,
        LocalDate endDate,
        String projectCode
        );

    List<DistancePrice> findAllByMachineInternalId(String machineInternalId);

  List<DistancePrice> findByProjectCode(String projectCode);

  boolean existsByMachine_Id(Long machineId);
}

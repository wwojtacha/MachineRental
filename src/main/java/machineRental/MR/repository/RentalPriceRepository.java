package machineRental.MR.repository;

import machineRental.MR.price.rental.model.RentalPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalPriceRepository extends JpaRepository<RentalPrice, String> {

    Page<RentalPrice> findByYearEqualsAndMachine_InternalIdContainingAndPriceTypeContaining(Integer year, String machineInternalId, String priceType, Pageable pageable);

    Page<RentalPrice> findByMachine_InternalIdContainingAndPriceTypeContaining(String machineInternalId, String priceType, Pageable pageable);

    boolean existsByMachine_Id(Long machineId);
}

package machineRental.MR.repository;

import machineRental.MR.price.rental.model.Price;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, String> {

    Page<Price> findByYearEqualsAndMachine_InternalIdContainingAndPriceTypeContaining(Integer year, String machineInternalId, String priceType, Pageable pageable);

    Page<Price> findByMachine_InternalIdContainingAndPriceTypeContaining(String machineInternalId, String priceType, Pageable pageable);

    boolean existsByMachine_Id(Long machineId);
}

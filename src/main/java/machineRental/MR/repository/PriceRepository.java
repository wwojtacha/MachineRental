package machineRental.MR.repository;

import machineRental.MR.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findByMachine_InternalIdAndYear(String internalId, Integer year);
}

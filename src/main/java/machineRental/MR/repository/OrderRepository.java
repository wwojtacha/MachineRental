package machineRental.MR.repository;

import java.time.LocalDate;
import machineRental.MR.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMachine_InternalId(String internalId);

    Page<Order> findByMachine_InternalIdContainingAndStatusEqualsAndStartDateBetweenAndEndDateBetweenAndPriceTypeContainingAndClient_NameContainingAndSeller_NameContaining(
        String machineInternalId, String status, LocalDate orderStartDateStart, LocalDate orderStartDateEnd,
        LocalDate orderEndDateStart, LocalDate orderEndDateEnd, String priceType, String clientName, String sellerName, Pageable pageable);

    Page<Order> findByMachine_InternalIdContainingAndStatusContainingAndStartDateBetweenAndEndDateBetweenAndPriceTypeContainingAndClient_NameContainingAndSeller_NameContaining(
        String machineInternalId, String status, LocalDate orderStartDateStart, LocalDate orderStartDateEnd,
        LocalDate orderEndDateStart, LocalDate orderEndDateEnd, String priceType, String clientName, String sellerName, Pageable pageable);

    List<Order> findByStartDateAfterAndMachine_InternalIdAndDbPriceTrue(LocalDate startDate, String machineInternalId);


}

package machineRental.MR.repository;

import machineRental.MR.machine.model.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findByInternalId(String internalId);

    boolean existsByInternalId(String internalId);

    Page<Machine> findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndProductionYearAndMachineStatusContainingAndMachineType_MachineTypeContaining(
            String internalId,
            String name,
            String producer,
            String model,
            Integer productionYear,
            String machineStatus,
            String machineType,
            Pageable pageable
    );

    Page<Machine> findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndMachineStatusContainingAndMachineType_MachineTypeContaining(
            String internalId,
            String name,
            String producer,
            String model,
            String machineStatus,
            String machineType,
            Pageable pageable
    );

}

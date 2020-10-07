package machineRental.MR.repository;

import machineRental.MR.machine.model.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findByInternalId(String internalId);

    boolean existsByInternalId(String internalId);

    Page<Machine> findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndProductionYearAndOwner_NameContainingAndMachineStatusContainingAndMachineType_MachineTypeContaining(
            String internalId,
            String name,
            String producer,
            String model,
            Integer productionYear,
            String owner,
            String machineStatus,
            String machineType,
            Pageable pageable
    );

    Page<Machine> findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndOwner_NameContainingAndMachineStatusContainingAndMachineType_MachineTypeContaining(
            String internalId,
            String name,
            String producer,
            String model,
            String owner,
            String machineStatus,
            String machineType,
            Pageable pageable
    );


}

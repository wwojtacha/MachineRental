package machineRental.MR.repository;

import machineRental.MR.machineType.model.MachineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineTypeRepository extends JpaRepository<MachineType, Long> {

    boolean existsByMachineType(String machineType);

    MachineType findByMachineType(String machineType);

    Page<MachineType> findByMachineTypeContaining(String type, Pageable pageable);

}

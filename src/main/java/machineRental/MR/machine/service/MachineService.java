package machineRental.MR.machine.service;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.MachineTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private MachineTypeRepository machineTypeRepository;


    public Machine create(Machine machine, BindingResult bindingResult) {

        if(machine.getMachineType() == null || !machineTypeRepository.existsByMachineType(machine.getMachineType().getMachineType())) {
            bindingResult.addError(new FieldError("machineType", "machineType", "Machine type must not be null and must be chosen from predefined values"));
        } else {
            Long machineTypeId = machineTypeRepository.findByMachineType(machine.getMachineType().getMachineType()).getId();
            machine.getMachineType().setId(machineTypeId);
        }

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }

        validateMachineInternalIdConsistency(machine.getInternalId(), null,  bindingResult);
        return machineRepository.save(machine);
    }

    public Page<Machine> search(String internalId, String name, String producer, String model, Integer productionYear, String machineStatus, String type, Pageable pageable) {

        if(productionYear == null) {
            return machineRepository.findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndMachineStatusContainingAndMachineType_MachineTypeContaining(internalId, name, producer, model, machineStatus, type, pageable);
        } else {
            return machineRepository.findByInternalIdContainingAndNameContainingAndProducerContainingAndModelContainingAndProductionYearAndMachineStatusContainingAndMachineType_MachineTypeContaining(internalId, name, producer, model, productionYear, machineStatus, type, pageable);
        }
    }


    public Machine getByInternalId(String internalId) {
        Optional<Machine> machine = Optional.ofNullable(machineRepository.findByInternalId(internalId));
        if (!machine.isPresent()) {
            throw new NotFoundException("Machine with internal id: " + internalId + " does not exist.");
        }
        return machine.get();
    }

    public Machine update(Long id, Machine machine, BindingResult bindingResult) {
        Optional<Machine> dbMachine = machineRepository.findById(id);
        if(!dbMachine.isPresent()) {
            throw new NotFoundException("Machine with internal id: " + machine.getInternalId() + " does not exist");
        }

        validateMachineInternalIdConsistency(machine.getInternalId(), dbMachine.get().getInternalId(), bindingResult);

        if (machine.getMachineType().getId() == null) {
            machine.getMachineType().setId(dbMachine.get().getMachineType().getId());
        }

        machine.setId(id);
        return machineRepository.save(machine);
    }



    private void validateMachineInternalIdConsistency(String internalId, String currentInternalId, BindingResult bindingResult) {
        if (machineRepository.existsByInternalId(internalId) && !internalId.equals(currentInternalId)) {
            bindingResult.addError(new FieldError(
                    "machine",
                    "internalId",
                    String.format("Machine with internal id \'%s\' already exists", internalId)
            ));

        }
        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }
    }
}

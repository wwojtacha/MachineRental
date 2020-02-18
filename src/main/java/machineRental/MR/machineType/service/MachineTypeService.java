package machineRental.MR.machineType.service;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machineType.model.MachineType;
import machineRental.MR.repository.MachineTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Service
public class MachineTypeService {

    @Autowired
    private MachineTypeRepository machineTypeRepository;

    public MachineType create(MachineType machineType, BindingResult bindingResult) {
        validateMachineTypeConsistency(machineType.getMachineType(), null, bindingResult);
        return machineTypeRepository.save(machineType);
    }


    public MachineType getByMachineType(String machineType) {
        Optional<MachineType> type = Optional.ofNullable(machineTypeRepository.findByMachineType(machineType));
        if(!type.isPresent()) {
            throw new NotFoundException("Machine type: " + "\'" + machineType + "\'" + " does not exist");
        }
        return type.get();
    }

    public Page<MachineType> search(String type, Pageable pageable) {
        return machineTypeRepository.findByMachineTypeContaining(type, pageable);
    }


    public MachineType update(Long id, MachineType machineType, BindingResult bindingResult) {
        Optional<MachineType> dbMachineType = machineTypeRepository.findById(id);
        if(!dbMachineType.isPresent()) {
            throw new NotFoundException("Machine type: " + "\'" + id + "\'" + " does not exist");
        }

        validateMachineTypeConsistency(machineType.getMachineType(), dbMachineType.get().getMachineType(), bindingResult);

        machineType.setId(id);
        return machineTypeRepository.save(machineType);
    }

    private void validateMachineTypeConsistency(String type, String currentType, BindingResult bindingResult) {

        if(machineTypeRepository.existsByMachineType(type) && !type.equals(currentType)) {
            bindingResult.addError(new FieldError(
                    "machineType",
                    "machineType",
                    String.format("Machine type: \'%s\' already exists", type)
            ));
        }
        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }
    }


}

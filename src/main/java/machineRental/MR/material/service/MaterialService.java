package machineRental.MR.material.service;

import java.util.Optional;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.material.model.Material;
import machineRental.MR.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public Material create(Material material, BindingResult bindingResult) {
        validateMaterialConsistency(material.getType(), null, bindingResult);
        return materialRepository.save(material);
    }


    public Material getByMaterialType(String materialType) {
        Optional<Material> material = Optional.ofNullable(materialRepository.findByType(materialType));
        if(!material.isPresent()) {
            throw new NotFoundException("Material type: " + "\'" + material + "\'" + " does not exist");
        }
        return material.get();
    }

    public Page<Material> search(String type, Pageable pageable) {
        return materialRepository.findByTypeContaining(type, pageable);
    }


    public Material update(Long id, Material type, BindingResult bindingResult) {
        Optional<Material> dbMaterial = materialRepository.findById(id);
        if(!dbMaterial.isPresent()) {
            throw new NotFoundException("Machine type: " + "\'" + id + "\'" + " does not exist");
        }

        validateMaterialConsistency(type.getType(), dbMaterial.get().getType(), bindingResult);

        type.setId(id);
        return materialRepository.save(type);
    }

    private void validateMaterialConsistency(String type, String currentType, BindingResult bindingResult) {

        if(materialRepository.existsByType(type) && !type.equals(currentType)) {
            bindingResult.addError(new FieldError(
                    "material",
                    "type",
                    String.format("Material: \'%s\' already exists", type)
            ));
        }
        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }
    }


}

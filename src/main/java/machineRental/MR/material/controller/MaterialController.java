package machineRental.MR.material.controller;

import javax.validation.Valid;
import machineRental.MR.material.model.Material;
import machineRental.MR.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Material create(
            @RequestBody @Valid Material material,
            BindingResult bindingResult
    ) {
        return materialService.create(material, bindingResult);
    }

    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    public Material getById(@PathVariable String type) {
        return materialService.getByMaterialType(type);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Material> search(
            @RequestParam(name = "material", required = false, defaultValue = "") String type,
            Pageable pageable
    ) {
        return materialService.search(type, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Material update(
            @PathVariable Long id,
            @RequestBody @Valid Material type,
            BindingResult bindingResult
    ) {
        return materialService.update(id, type, bindingResult);
    }

}

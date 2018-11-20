package machineRental.MR.controller;

import machineRental.MR.model.MachineType;
import machineRental.MR.service.MachineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/machineTypes")
public class MachineTypeController {

    @Autowired
    private MachineTypeService machineTypeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MachineType create(
            @RequestBody @Valid MachineType machineType,
            BindingResult bindingResult
    ) {
        return machineTypeService.create(machineType, bindingResult);
    }

    @GetMapping("/{type}")
    @ResponseStatus(HttpStatus.OK)
    public MachineType getById(@PathVariable String type) {
        return machineTypeService.getByMachineType(type);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MachineType> search(
            @RequestParam(name = "machineType", required = false, defaultValue = "") String id,
            Pageable pageable
    ) {
        return machineTypeService.search(id, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MachineType update(
            @PathVariable Integer id,
            @RequestBody @Valid MachineType machineType,
            BindingResult bindingResult
    ) {
        return machineTypeService.update(id, machineType, bindingResult);
    }

}

package machineRental.MR.machine.controller;

import machineRental.MR.machine.model.Machine;
import machineRental.MR.machine.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/machines")
public class MachineController {

    @Autowired
    private MachineService machineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Machine create(
            @RequestBody @Valid Machine machine,
            BindingResult bindingResult
    ) {
        return machineService.create(machine, bindingResult);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<Machine> search(
            @RequestParam(value = "internalId", required = false, defaultValue = "") String internalId,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "producer", required = false, defaultValue = "") String producer,
            @RequestParam(value = "model", required = false, defaultValue = "") String model,
            @RequestParam(value = "productionYear", required = false) Integer productionYear,
            @RequestParam(value = "owner", required = false, defaultValue = "") String owner,
            @RequestParam(value = "machineStatus", required = false, defaultValue = "") String machineStatus,
            @RequestParam(value = "machineType", required = false, defaultValue = "") String machineType,
            Pageable pageable
            ){
        return machineService.search(internalId, name, producer, model, productionYear, owner, machineStatus, machineType, pageable);
    }

    @GetMapping("/{internalId}")
    @ResponseStatus(HttpStatus.OK)
    public Machine getByInternalId(@PathVariable String internalId) {
        return machineService.getByInternalId(internalId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Machine update(@PathVariable Long id, @RequestBody @Valid Machine machine, BindingResult bindingResult) {
        return machineService.update(id, machine, bindingResult);
    }

}

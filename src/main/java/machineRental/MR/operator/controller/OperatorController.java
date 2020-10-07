package machineRental.MR.operator.controller;

import javax.validation.Valid;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.operator.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/operators")
public class OperatorController {

  @Autowired
  private OperatorService operatorService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Operator create(@RequestBody @Valid Operator operator, BindingResult bindingResult) {
    return operatorService.create(operator, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<Operator> search(
      @RequestParam(name = "name", required = false, defaultValue = "") String name,
      @RequestParam(name = "qualifications", required = false, defaultValue = "") String qualifications,
      @RequestParam (name = "companyMpk", required = false, defaultValue = "") String companyMpk,
      Pageable pageable
      ) {

    return operatorService.search(name, qualifications, companyMpk, pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Operator getById(@PathVariable Long id) {
    return operatorService.getById(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Operator update(@PathVariable Long id, @RequestBody @Valid Operator operator, BindingResult bindingResult) {
    return operatorService.update(id, operator, bindingResult);
  }


}

package machineRental.MR.costcode.controller;

import javax.validation.Valid;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.costcode.service.CostCodeService;
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
@RequestMapping("/codes")
public class CostCodeController {


  @Autowired
  private CostCodeService costCodeService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CostCode create (@RequestBody @Valid CostCode costCode, BindingResult bindingResult) {
    return costCodeService.create(costCode, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<CostCode> search(
      @RequestParam(name = "projectCode", required = false, defaultValue = "") String projectCode,
      @RequestParam(name = "costType", required = false, defaultValue = "") String costType,
      Pageable pageable) {

    return costCodeService.search(projectCode, costType, pageable);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CostCode update(@PathVariable Long id, @RequestBody @Valid CostCode costCode, BindingResult bindingResult) {
    return costCodeService.update(id, costCode, bindingResult);
  }

}

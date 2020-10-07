package machineRental.MR.estimate.controller;

import javax.validation.Valid;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.estimate.service.EstimatePositionService;
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
import org.springframework.web.multipart.MultipartFile;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/estimatePositions")
public class EstimateController {


  @Autowired
  private EstimatePositionService estimatePositionService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadFile(@RequestBody MultipartFile file) {
    estimatePositionService.saveDataFromExcel(file);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Page<EstimatePosition> search(
      @RequestParam(name = "name", required = false, defaultValue = "") String name,
      @RequestParam(name = "projectCode", required = false, defaultValue = "") String projectCode,
      @RequestParam(name = "costType", required = false, defaultValue = "") String costType,
      @RequestParam(name = "remarks", required = false, defaultValue = "") String remarks,
      Pageable pageable) {

    return estimatePositionService.search(name, projectCode, costType, remarks, pageable);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public EstimatePosition update(@PathVariable Long id, @RequestBody @Valid EstimatePosition estimatePosition, BindingResult bindingResult) {
    return estimatePositionService.update(id, estimatePosition, bindingResult);
  }

}

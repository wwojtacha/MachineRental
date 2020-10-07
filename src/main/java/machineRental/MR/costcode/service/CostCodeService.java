package machineRental.MR.costcode.service;

import java.util.Optional;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.repository.CostCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class CostCodeService {

  @Autowired
  private CostCodeRepository costCodeRepository;


  public CostCode create(CostCode costCode, BindingResult bindingResult) {

    validateCostCodeConsistency(costCode, null, bindingResult);

    costCode.setFullCode(costCode.getProjectCode() + "-" + costCode.getCostType());
    return costCodeRepository.save(costCode);
  }

  public Page<CostCode> search(String projectCode, String costType, Pageable pageable) {
    return costCodeRepository.findByProjectCodeContainingAndCostTypeContaining(projectCode, costType, pageable);
  }

  public CostCode update(Long id, CostCode costCode, BindingResult bindingResult) {
    Optional<CostCode> dbCostCode = costCodeRepository.findById(id);
    if(!dbCostCode.isPresent()) {
      throw new NotFoundException("Cost code: " + "\'" + id + "\'" + " does not exist");
    }

    validateCostCodeConsistency(costCode, dbCostCode.get(), bindingResult);

    costCode.setId(id);
    costCode.setFullCode(costCode.getProjectCode() + "-" + costCode.getCostType());
    return costCodeRepository.save(costCode);
  }

  private void validateCostCodeConsistency(CostCode costCode, CostCode currentCostCode, BindingResult bindingResult) {

    String newFullCode = costCode.getFullCode();

    String currentFullCode = "";

    if (currentCostCode != null) {
      currentFullCode = currentCostCode.getFullCode();
    }

    if(existsByFullCode(costCode.getFullCode())
        && !newFullCode.equals(currentFullCode)) {
      bindingResult.addError(new FieldError(
          "cotCode",
          "codtCode",
          String.format("Cost code: \'%s\' already exists", newFullCode)
      ));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public boolean existsByFullCode(String fullCode) {
    return costCodeRepository.existsByFullCode(fullCode);
  }

  public CostCode getByFullCode(String fullCode) {
    return costCodeRepository.findByFullCode(fullCode);
  }
}

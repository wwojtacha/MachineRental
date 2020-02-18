package machineRental.MR.operator.service;

import static java.lang.String.format;

import java.util.Optional;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.repository.ClientRepository;
import machineRental.MR.repository.OperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class OperatorService {

  @Autowired
  private OperatorRepository operatorRepository;

  @Autowired
  private ClientRepository clientRepository;

  public Operator create(Operator operator, BindingResult bindingResult) {

    validateOperatorNameConsistency(operator.getName(), null, bindingResult);
    validateClient(operator);

    return operatorRepository.save(operator);
  }

  public Page<Operator> search(String name, String qualifications, String companyMpk, Pageable pageable) {

    return operatorRepository.findByNameContainingAndQualificationsContainingAndCompany_MpkContaining(name, qualifications, companyMpk, pageable);
  }

  public Operator getById(Long id) {
    Optional<Operator> dbOperator = operatorRepository.findById(id);

    if (!dbOperator.isPresent()) {
      throw new NotFoundException(String.format("Operator with id: \'%s\' does not exist.", id));
    }

    return dbOperator.get();
  }

  public Operator update(Long id, Operator operator, BindingResult bindingResult) {
    Optional<Operator> dbOperator = operatorRepository.findById(id);
    if(!dbOperator.isPresent()) {
      throw new NotFoundException(String.format("Operator with id: \'%s\' does not exist", id));
    }

    validateOperatorNameConsistency(operator.getName(), dbOperator.get().getName(), bindingResult);
    validateClient(operator);

    operator.setId(id);
    return operatorRepository.save(operator);
  }

  private void validateOperatorNameConsistency(String operatorName, String currentOperatorName, BindingResult bindingResult) {
    if (operatorRepository.existsByName(operatorName) && !operatorName.equals(currentOperatorName)) {
      bindingResult.addError(new FieldError(
          "operator",
          "name",
          String.format("Operator with name \'%s\' already exists.", operatorName)));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  private void validateClient(Operator operator) {

    String mpk = operator.getCompany().getMpk();

    if (clientRepository.existsByMpk(mpk)) {
      Long clientId = clientRepository.findByMpk(mpk).getId();
      operator.getCompany().setId(clientId);
    } else {
      throw new NotFoundException(format("Company with MPK/NIP \'%s\' does not exist.", mpk));
    }
  }
}

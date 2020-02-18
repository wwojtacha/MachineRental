package machineRental.MR.workDocument.validator;

import javax.validation.constraints.NotNull;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OperatorRepository;
import machineRental.MR.repository.WorkDocumentRepository;
import machineRental.MR.workDocument.model.WorkDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class WorkDocumentValidator {

  @Autowired
  private OperatorRepository operatorRepository;

  @Autowired
  private MachineRepository machineRepository;


  public void validateWorkDocument(WorkDocument workDocument, BindingResult bindingResult) {
    Operator operator = workDocument.getOperator();
    Machine machine = workDocument.getMachine();

    validateOperator(operator);
    validateMachine(machine);
    validateCounterStates(workDocument, bindingResult);

  }

  private void validateOperator(Operator operator) {
    String operatorName = operator.getName();

    if (!operatorRepository.existsByName(operatorName)) {
      throw new NotFoundException(String.format("Operator with name \'%s\' does not exist.", operatorName));
    }
  }

  private void validateMachine(Machine machine) {
    String machineInternalId = machine.getInternalId();

    if (!machineRepository.existsByInternalId(machineInternalId)) {
      throw new NotFoundException(String.format("Machine with number \'%s\' does not exist.", machineInternalId));
    }
  }

  private void validateCounterStates(WorkDocument workDocument, BindingResult bindingResult) {
    int counterStart = workDocument.getCounterStart();
    int counterEnd = workDocument.getCounterEnd();

    if (!isCounterEndGreaterThan(counterEnd, counterStart) || !isCounterStatePositive(counterStart, counterEnd)) {
      bindingResult.addError(new FieldError(
          "workDocumentEntry",
          "counterEnd",
          "End counter state cannot be lower than start counter state."));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  private boolean isCounterEndGreaterThan(int counterEnd, int counterStart) {
    return counterEnd >= counterStart;
  }

  private boolean isCounterStatePositive(int counterStart, int counterEnd) {
    return counterStart >= 0 && counterEnd >= 0;
  }

}

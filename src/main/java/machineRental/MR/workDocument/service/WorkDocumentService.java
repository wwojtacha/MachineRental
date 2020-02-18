package machineRental.MR.workDocument.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import machineRental.MR.workDocument.DocumentType;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.repository.MachineRepository;
import machineRental.MR.repository.OperatorRepository;
import machineRental.MR.repository.WorkDocumentRepository;
import machineRental.MR.workDocument.validator.WorkDocumentValidator;
import machineRental.MR.workDocumentEntry.WorkDocumentEntryHelper;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class WorkDocumentService {

  @Autowired
  private WorkDocumentRepository workDocumentRepository;

  @Autowired
  private WorkReportEntryService workReportEntryService;

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  @Autowired
  private WorkDocumentValidator workDocumentValidator;


  public WorkDocument create(WorkDocument workDocument, BindingResult bindingResult) {

    validateWorkDocumentConsistencyById(workDocument.getId(), null, bindingResult);
    workDocumentValidator.validateWorkDocument(workDocument, bindingResult);

    return workDocumentRepository.save(workDocument);
  }

  public Page<WorkDocument> search(String id, List<DocumentType> documentType, LocalDate date, String operatorName, String machineInternalId, Pageable pageable) {

    if (isEmpty(documentType)) {
      documentType = new ArrayList<>(EnumSet.allOf(DocumentType.class));
    }

    if (date == null) {
      return workDocumentRepository.findByIdContainingAndDocumentTypeInAndOperator_NameContainingAndMachine_InternalIdContainingOrderByDate(id, documentType, operatorName, machineInternalId, pageable);
    }

    return workDocumentRepository.findByIdContainingAndDocumentTypeInAndDateEqualsAndOperator_NameContainingAndMachine_InternalIdContainingOrderByDate(id, documentType, date, operatorName, machineInternalId, pageable);
  }

  public WorkDocument getById(String id) {
    Optional<WorkDocument> dbWorkDocument = workDocumentRepository.findById(id);

    if (!dbWorkDocument.isPresent()) {
      throw new NotFoundException(String.format("Work document with MPK: \'%s\' does not exist", id));
    }

    return dbWorkDocument.get();
  }

  public WorkDocument update(String id, WorkDocument workDocument, BindingResult bindingResult) {

    validateWorkDocumentConsistencyById(workDocument.getId(), id, bindingResult);
    workDocumentValidator.validateWorkDocument(workDocument, bindingResult);

    workDocument.setId(id);
    return workDocumentRepository.save(workDocument);
  }

  private void validateWorkDocumentConsistencyById(String id, String currentId, BindingResult bindingResult) {
    if(workDocumentRepository.existsById(id) && !id.equals(currentId)) {
      bindingResult.addError(new FieldError(
          "workDocumentEntry",
          "id",
          String.format("Work document with id: \'%s\' already exists", id)));
    }

    if(bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void delete(String workDocumentId) {
    Optional<WorkDocument> dbWorkDocument = workDocumentRepository.findById(workDocumentId);

    if (!dbWorkDocument.isPresent()) {
      throw new NotFoundException(String.format("Work document with id \'%s\' doesn`t exist!", workDocumentId));
    }

    DocumentType documentType = dbWorkDocument.get().getDocumentType();

    if (DocumentType.WORK_REPORT == documentType) {
      workReportEntryService.deleteByWorkDocument(workDocumentId);
    } else if (DocumentType.ROAD_CARD == documentType) {
      roadCardEntryService.deleteByWorkDocument(workDocumentId);
    }

    workDocumentRepository.deleteById(workDocumentId);

  }
}

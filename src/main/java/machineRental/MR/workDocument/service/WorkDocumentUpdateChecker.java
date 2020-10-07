package machineRental.MR.workDocument.service;

import machineRental.MR.exception.BindingResultException;
import machineRental.MR.workDocument.model.WorkDocument;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

//@Service
public interface WorkDocumentUpdateChecker {

  void checkOnUpdate(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument, BindingResult bindingResult);

  default boolean isSameOperator(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    return dbWorkDocument.getOperator().getId() == editedWorkDocument.getOperator().getId();
  }

  default boolean isSameMachine(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    return dbWorkDocument.getMachine().getInternalId().equals(editedWorkDocument.getMachine().getInternalId());
  }

  default boolean isSameDate(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    return dbWorkDocument.getDate().isEqual(editedWorkDocument.getDate());
  }

  default void checkDocumentType(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument, BindingResult bindingResult) {

    if (!isSameDocumentType(dbWorkDocument, editedWorkDocument)) {
      bindingResult.addError(new FieldError(
          "workDocument",
          "documentType",
          String.format("Document type cannot be changed because there are already entries on work document: %s", dbWorkDocument.getId())));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  default boolean isSameDocumentType(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument) {
    return dbWorkDocument.getDocumentType() == editedWorkDocument.getDocumentType();
  }
}

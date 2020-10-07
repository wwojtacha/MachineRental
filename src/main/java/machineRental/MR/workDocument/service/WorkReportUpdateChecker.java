package machineRental.MR.workDocument.service;

import java.util.List;
import machineRental.MR.workDocument.DocumentType;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.model.WorkDocumentEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.WorkReportEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class WorkReportUpdateChecker implements WorkDocumentUpdateChecker {

  @Autowired
  private WorkReportEntryService workReportEntryService;


  @Override
  public void checkOnUpdate(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument, BindingResult bindingResult) {

    String dbWorkDocumentId = dbWorkDocument.getId();
    List<WorkReportEntry> workReportEntries = workReportEntryService.getEntriesFor(dbWorkDocumentId);

    if (workReportEntries.isEmpty()) {
      return;
    }

    checkDocumentType(dbWorkDocument, editedWorkDocument, bindingResult);

//    need to set editedWorkDocument for following lookups in checkers so as not to check for "old" values
    for (WorkReportEntry workReportEntry : workReportEntries) {
      workReportEntry.setWorkDocument(editedWorkDocument);
    }

    if (!isSameOperator(dbWorkDocument, editedWorkDocument) && isSameMachine(dbWorkDocument, editedWorkDocument) && isSameDate(dbWorkDocument, editedWorkDocument)) {
      workReportEntryService.checkWorkingTime(workReportEntries, bindingResult);
    } else if (!isSameMachine(dbWorkDocument, editedWorkDocument) || !isSameDate(dbWorkDocument, editedWorkDocument)) {
      workReportEntryService.checkWorkingTime(workReportEntries, bindingResult);
      workReportEntryService.checkPrices(dbWorkDocument, editedWorkDocument);
    } else {
      return;
    }

  }


}

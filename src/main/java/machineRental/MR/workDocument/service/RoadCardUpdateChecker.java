package machineRental.MR.workDocument.service;

import java.util.List;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.workDocumentEntry.service.RoadCardEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class RoadCardUpdateChecker implements WorkDocumentUpdateChecker {

  @Autowired
  private RoadCardEntryService roadCardEntryService;

  @Override
  public void checkOnUpdate(WorkDocument dbWorkDocument, WorkDocument editedWorkDocument, BindingResult bindingResult) {

    String dbWorkDocumentId = dbWorkDocument.getId();
    List<RoadCardEntry> roadCardEntries = roadCardEntryService.getEntriesFor(dbWorkDocumentId);

    if (roadCardEntries.isEmpty()) {
      return;
    }

    checkDocumentType(dbWorkDocument, editedWorkDocument, bindingResult);

    //    need to set editedWorkDocument for following lookups in checkers so as not to check for "old" values
    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      roadCardEntry.setWorkDocument(editedWorkDocument);
    }

    if (!isSameOperator(dbWorkDocument, editedWorkDocument) && isSameMachine(dbWorkDocument, editedWorkDocument) && isSameDate(dbWorkDocument, editedWorkDocument)) {
      roadCardEntryService.checkWorkingTime(roadCardEntries, bindingResult);
    } else if (!isSameMachine(dbWorkDocument, editedWorkDocument) || !isSameDate(dbWorkDocument, editedWorkDocument)) {
      roadCardEntryService.checkWorkingTime(roadCardEntries, bindingResult);
      roadCardEntryService.checkPrices(dbWorkDocument, editedWorkDocument);
    } else {
      return;
    }

  }
}

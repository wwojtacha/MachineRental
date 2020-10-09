package machineRental.MR.workDocument.service;

import java.util.Map;
import lombok.AllArgsConstructor;
import machineRental.MR.workDocument.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
@AllArgsConstructor
public class WorkDocumentCheckerProvider {

//  @Autowired
  private WorkReportUpdateChecker workReportUpdateChecker;

//  @Autowired
  private RoadCardUpdateChecker roadCardUpdateChecker;

//  @Autowired
  private Map<DocumentType, WorkDocumentUpdateChecker> WORK_DOCUMENT_CHECKERS;

//  static {
//    WORK_DOCUMENT_CHECKERS.put(DocumentType.WORK_REPORT, workReportUpdateChecker);
//    WORK_DOCUMENT_CHECKERS.put(DocumentType.ROAD_CARD, new RoadCardUpdateChecker());
//  }

  WorkDocumentUpdateChecker chooseChecker(DocumentType documentType) {
//    WORK_DOCUMENT_CHECKERS.put(DocumentType.WORK_REPORT, workReportUpdateChecker);
//    WORK_DOCUMENT_CHECKERS.put(DocumentType.ROAD_CARD, roadCardUpdateChecker);

    return WORK_DOCUMENT_CHECKERS.get(documentType);
  }

}

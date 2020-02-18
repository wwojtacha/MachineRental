package machineRental.MR.workDocumentEntry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.model.WorkDocumentEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;

@Data
public class WorkDocumentEntryForValidation {

  private Long id;

  private WorkCode workCode;

  private LocalTime startHour;

  private LocalTime endHour;

  private String mpk;

  private String acceptingPerson;

  private WorkDocument workDocument;

  public static <T extends WorkDocumentEntry> WorkDocumentEntryForValidation fromWorkDocumentEntry(T workReportEntry) {

    WorkDocumentEntryForValidation workDocumentEntryForValidation = new WorkDocumentEntryForValidation();

    workDocumentEntryForValidation.setId(workReportEntry.getId());
    workDocumentEntryForValidation.setWorkCode(workReportEntry.getWorkCode());
    workDocumentEntryForValidation.setStartHour(workReportEntry.getStartHour());
    workDocumentEntryForValidation.setEndHour(workReportEntry.getEndHour());
    workDocumentEntryForValidation.setMpk(workReportEntry.getMpk());
    workDocumentEntryForValidation.setAcceptingPerson(workReportEntry.getAcceptingPerson());
    workDocumentEntryForValidation.setWorkDocument(workReportEntry.getWorkDocument());

    return workDocumentEntryForValidation;
  }

  public static Collection<WorkDocumentEntryForValidation> fromWorkReportEntries(Collection<WorkReportEntry> workReportEntries) {
    List<WorkDocumentEntryForValidation> workDocumentsEntriesForValidation = new ArrayList<>();

    for (WorkReportEntry workReportEntry : workReportEntries) {
      workDocumentsEntriesForValidation.add(WorkDocumentEntryForValidation.fromWorkDocumentEntry(workReportEntry));
    }

    return workDocumentsEntriesForValidation;
  }

  public static Collection<WorkDocumentEntryForValidation> fromRoadCardEntries(Collection<RoadCardEntry> roadCardEntries) {
    List<WorkDocumentEntryForValidation> workDocumentsEntriesForValidation = new ArrayList<>();

    for (RoadCardEntry roadCardEntry : roadCardEntries) {
      workDocumentsEntriesForValidation.add(WorkDocumentEntryForValidation.fromWorkDocumentEntry(roadCardEntry));
    }

    return workDocumentsEntriesForValidation;
  }

}

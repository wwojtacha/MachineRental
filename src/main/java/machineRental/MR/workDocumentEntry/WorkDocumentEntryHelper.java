package machineRental.MR.workDocumentEntry;

import java.time.LocalDate;
import java.util.List;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.model.WorkDocumentEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import machineRental.MR.repository.RoadCardEntryRepository;
import machineRental.MR.repository.WorkReportEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkDocumentEntryHelper {

  @Autowired
  private WorkReportEntryRepository workReportEntryRepository;

  @Autowired
  private RoadCardEntryRepository roadCardEntryRepository;


  public <T extends WorkDocumentEntry> List<WorkReportEntry> getWorkReportEntriesByOperator(List<T> workReportEntries) {
    WorkDocument workDocument = getWorkDocument(workReportEntries);
    Operator operator = workDocument.getOperator();
    LocalDate date = getDate(workDocument);

    return workReportEntryRepository.findByWorkDocument_OperatorAndWorkDocument_DateEquals(operator, date);
  }

  public <T extends WorkDocumentEntry> List<WorkReportEntry> getWorkReportEntriesByMachine(List<T> workReportEntries) {
    WorkDocument workDocument = getWorkDocument(workReportEntries);
    Machine machine = getMachine(workDocument);
    LocalDate date = getDate(workDocument);

    return workReportEntryRepository.findByWorkDocument_MachineAndWorkDocument_DateEquals(machine, date);
  }

  public <T extends WorkDocumentEntry> List<RoadCardEntry> getRoadCardEntriesByOperator(List<T> roadCardEntries) {
    WorkDocument workDocument = getWorkDocument(roadCardEntries);
    Operator operator = workDocument.getOperator();
    LocalDate date = getDate(workDocument);

    return roadCardEntryRepository.findByWorkDocument_OperatorAndWorkDocument_DateEquals(operator, date);
  }

  public <T extends WorkDocumentEntry> List<RoadCardEntry> getRoadCardEntriesByMachine(List<T> roadCardEntries) {
    WorkDocument workDocument = getWorkDocument(roadCardEntries);
    Machine machine = getMachine(workDocument);
    LocalDate date = getDate(workDocument);

    return roadCardEntryRepository.findByWorkDocument_MachineAndWorkDocument_DateEquals(machine, date);
  }

  private LocalDate getDate(WorkDocument workDocument) {
    return workDocument.getDate();
  }

  private Machine getMachine(WorkDocument workDocument) {
    return workDocument.getMachine();
  }

  private <T extends WorkDocumentEntry> WorkDocument getWorkDocument(List<T> workDocumentEntries) {
    return workDocumentEntries.get(0).getWorkDocument();
  }


}

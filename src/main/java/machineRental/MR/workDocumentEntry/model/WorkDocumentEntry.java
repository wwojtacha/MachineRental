package machineRental.MR.workDocumentEntry.model;

import java.time.LocalTime;
import lombok.Data;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;

@Data
public class WorkDocumentEntry {

  private Long id;

  private WorkCode workCode;

  private LocalTime startHour;

  private LocalTime endHour;

  private String mpk;

  private String acceptingPerson;

  private WorkDocument workDocument;

}

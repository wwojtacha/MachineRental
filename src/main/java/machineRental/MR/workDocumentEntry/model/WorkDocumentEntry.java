package machineRental.MR.workDocumentEntry.model;

import java.time.LocalTime;
import lombok.Data;
import machineRental.MR.costcode.model.CostCode;
import machineRental.MR.estimate.model.EstimatePosition;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.WorkCode;

@Data
public class WorkDocumentEntry {

  private Long id;

  private WorkCode workCode;

  private LocalTime startHour;

  private LocalTime endHour;

  private EstimatePosition estimatePosition;

  private CostCode costCode;

  private Operator acceptingPerson;

  private WorkDocument workDocument;

}

package machineRental.MR.workDocumentEntry;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.machine.model.Machine;
import machineRental.MR.operator.model.Operator;
import machineRental.MR.workDocument.model.WorkDocument;
import machineRental.MR.workDocumentEntry.model.RoadCardEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class WorkDocumentEntryValidator {

  public static final Set<WorkCode> EXPLOITATION_WORK_CODES = Collections.unmodifiableSet(EnumSet.of(WorkCode.PS, WorkCode.PX, WorkCode.CP));


  public void validateWorkingTime(Collection<WorkDocumentEntryForValidation> workDocumentEntriesForValidation, Collection<WorkDocumentEntryForValidation> allWorkDocumentEntriesForValidation, BindingResult bindingResult) {


    ALL_WORK_REPORT_ENTRIES:
    for (WorkDocumentEntryForValidation workDocumentEntry : workDocumentEntriesForValidation) {

      checkSameEntryWorkingHours(workDocumentEntry, bindingResult);

      WorkDocument currentWorkDocument = workDocumentEntry.getWorkDocument();
      Operator currentOperator = currentWorkDocument.getOperator();
      Machine currentMachine = currentWorkDocument.getMachine();

      for (WorkDocumentEntryForValidation workDocumentEntryToCheck : allWorkDocumentEntriesForValidation) {

        if ((workDocumentEntry.getId() != null && workDocumentEntry.getId().equals(workDocumentEntryToCheck.getId())) || workDocumentEntry == workDocumentEntryToCheck) {
          continue;
        }

        if (isActivityTimeOverlapping(workDocumentEntry, workDocumentEntryToCheck)) {

          WorkDocument workDocumentToCheck = workDocumentEntryToCheck.getWorkDocument();
          Operator operatorToCheck = workDocumentToCheck.getOperator();
          Machine machineToCheck = workDocumentToCheck.getMachine();

// working time of operator with name "XXX" is allowed to overlap in time
          if (currentOperator.getName().equals(operatorToCheck.getName()) && !currentOperator.getName().toUpperCase().equals("XXX")) {
            bindingResult.addError(new FieldError(
                "workReportEntryOperator",
                "workHour",
                String.format("Working time of operator %s overlaps in time with his/her other activity for this day.", operatorToCheck.getName())));
            break ALL_WORK_REPORT_ENTRIES;
          } else if (currentMachine.getInternalId().equals(machineToCheck.getInternalId()) && isWorkCodeOverlapping(workDocumentEntry, workDocumentEntryToCheck)) {
            bindingResult.addError(new FieldError(
                "workReportEntryMachine",
                "workHour",
                String.format("Working time of machine %s overlaps in time with its other exploitation activity for this day.", machineToCheck.getInternalId())));
            break ALL_WORK_REPORT_ENTRIES;
          }
        } else {
          continue;
        }
      }
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  private void checkSameEntryWorkingHours(WorkDocumentEntryForValidation workDocumentEntry, BindingResult bindingResult) {
    LocalTime startHour = workDocumentEntry.getStartHour();
    LocalTime endHour = workDocumentEntry.getEndHour();

    if (endHour.isBefore(startHour) || endHour.equals(startHour)) {
      bindingResult.addError(new FieldError("workDocumentEntry", "endHour", "End hour cannot be lower or equal to start hour"));
    }
    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  private boolean isActivityTimeOverlapping(WorkDocumentEntryForValidation workDocumentEntry, WorkDocumentEntryForValidation workDocumentEntryToCheck) {
    return isStartHourOverlapping(workDocumentEntry, workDocumentEntryToCheck)
        || isEndHourOverlapping(workDocumentEntry, workDocumentEntryToCheck)
        || areHoursOverlapping(workDocumentEntry, workDocumentEntryToCheck);
  }

  private boolean isEndHourOverlapping(WorkDocumentEntryForValidation workDocumentEntry, WorkDocumentEntryForValidation workDocumentEntryToCheck) {
    LocalTime workReportEntryStartHour = workDocumentEntry.getStartHour();
    LocalTime workReportEntryEndHour = workDocumentEntry.getEndHour();
    LocalTime workReportEntryToCheckEndHour = workDocumentEntryToCheck.getEndHour();
    return workReportEntryToCheckEndHour.isAfter(workReportEntryStartHour) && workReportEntryToCheckEndHour.isBefore(workReportEntryEndHour)
        || workReportEntryEndHour.equals(workReportEntryToCheckEndHour);
  }

  private boolean isStartHourOverlapping(WorkDocumentEntryForValidation workDocumentEntry, WorkDocumentEntryForValidation workDocumentEntryToCheck) {
    LocalTime workReportEntryStartHour = workDocumentEntry.getStartHour();
    LocalTime workReportEntryEndHour = workDocumentEntry.getEndHour();
    LocalTime workReportEntryToCheckStartHour = workDocumentEntryToCheck.getStartHour();
    return workReportEntryToCheckStartHour.isAfter(workReportEntryStartHour) && workReportEntryToCheckStartHour.isBefore(workReportEntryEndHour)
        || workReportEntryStartHour.equals(workReportEntryToCheckStartHour);
  }

  private boolean areHoursOverlapping(WorkDocumentEntryForValidation workDocumentEntry, WorkDocumentEntryForValidation workDocumentEntryToCheck) {
    LocalTime workReportEntryStartHour = workDocumentEntry.getStartHour();
    LocalTime workReportEntryEndHour = workDocumentEntry.getEndHour();
    LocalTime workReportEntryToCheckEndHour = workDocumentEntryToCheck.getEndHour();
    LocalTime workReportEntryToCheckStartHour = workDocumentEntryToCheck.getStartHour();
    return workReportEntryToCheckStartHour.isBefore(workReportEntryStartHour) && workReportEntryToCheckEndHour.isAfter(workReportEntryEndHour);
  }

  private boolean isWorkCodeOverlapping(WorkDocumentEntryForValidation workDocumentEntry, WorkDocumentEntryForValidation workDocumentEntryToCheck) {
    return EXPLOITATION_WORK_CODES.contains(workDocumentEntry.getWorkCode()) && EXPLOITATION_WORK_CODES.contains(workDocumentEntryToCheck.getWorkCode());
  }

  public void validateWorkReportEntryQuantity(WorkReportEntry workReportEntry, BindingResult bindingResult) {
    double workQuantity = workReportEntry.getWorkQuantity();
    if (workQuantity < 0) {
      bindingResult.addError(new FieldError("workReportEntry", "workQuantity", "Work quantity cannot be lower than 0."));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void validateRoadCardEntryQuantity(RoadCardEntry roadCardEntry, BindingResult bindingResult) {
    double quantity = roadCardEntry.getQuantity();
    if (quantity < 0) {
      bindingResult.addError(new FieldError("roadCardEntry", "quantity", "Transported material/item quantity cannot be lower than 0."));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

  public void validateRoadCardEntryDistance(RoadCardEntry roadCardEntry, BindingResult bindingResult) {
    double distance = roadCardEntry.getDistance();
    if (distance < 0) {
      bindingResult.addError(new FieldError("roadCardEntry", "distance", "Distance cannot be lower than 0."));
    }

    if (bindingResult.hasErrors()) {
      throw new BindingResultException(bindingResult);
    }
  }

}

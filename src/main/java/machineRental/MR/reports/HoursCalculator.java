package machineRental.MR.reports;

import java.time.Duration;
import java.time.LocalTime;
import machineRental.MR.workDocumentEntry.model.WorkDocumentEntry;
import machineRental.MR.workDocumentEntry.model.WorkReportEntry;

public class HoursCalculator {

  public double getFirstDayNumberOfHours(WorkDocumentEntry workDocumentEntry) {

    double oneDayNumberOfHours = getOneDayNumberOfHours(workDocumentEntry);
    double result = oneDayNumberOfHours;

    if (isTwoDayEntry(oneDayNumberOfHours)) {
      double twoDayNumberOfHours = getTwoDayNumberOfHours(oneDayNumberOfHours);
      double secondDayNumberOfHours = getSecondDayNumberOfHours(workDocumentEntry);

      result = twoDayNumberOfHours - secondDayNumberOfHours;
    }

    return result;
  }

  public double getSecondDayNumberOfHours(WorkDocumentEntry workDocumentEntry) {

    double result = 0;
    double oneDayNumberOfHours = getOneDayNumberOfHours(workDocumentEntry);

    if (isTwoDayEntry(oneDayNumberOfHours)) {
      result = (double) Duration.between(LocalTime.of(00, 00), workDocumentEntry.getEndHour()).toSeconds() / 3600;
    }

    return result;
  }

  public double getNumberOfHours(WorkDocumentEntry workDocumentEntry) {
    double oneDayNumberOfHours = getOneDayNumberOfHours(workDocumentEntry);
    double result = oneDayNumberOfHours;

    if (isTwoDayEntry(oneDayNumberOfHours)) {
      result = getTwoDayNumberOfHours(oneDayNumberOfHours);
    }

    return result;
  }

  private double getOneDayNumberOfHours(WorkDocumentEntry workDocumentEntry) {
    return (double) Duration.between(workDocumentEntry.getStartHour(), workDocumentEntry.getEndHour()).toSeconds() / 3600;
  }

  private boolean isTwoDayEntry(double numberOfHours) {
    return numberOfHours < 0;
  }

  private double getTwoDayNumberOfHours(double numberOfHours) {
    return 24 - Math.abs(numberOfHours);
  }

}

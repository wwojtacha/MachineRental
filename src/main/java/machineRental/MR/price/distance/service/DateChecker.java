package machineRental.MR.price.distance.service;

import java.time.LocalDate;
import machineRental.MR.price.hour.exception.IncorrectDateException;

public class DateChecker {

  public DateChecker() {
  }

  public <T extends DateCheckerObject> boolean areDatesOverlapping(T newPrice, T createdPrice) {

    return isStartDateOverlapping(newPrice, createdPrice) || isEndDateOverlapping(newPrice, createdPrice) || areDatesInBetween(newPrice, createdPrice);
  }

  private <T extends DateCheckerObject> boolean isStartDateOverlapping(T newPrice, T createdPrice) {
    LocalDate newHourPriceStartDate = newPrice.getStartDate();
    LocalDate createdHourPriceStartDate = createdPrice.getStartDate();
    LocalDate createdHourPriceEndDate = createdPrice.getEndDate();

    return newHourPriceStartDate.isAfter(createdHourPriceStartDate) && newHourPriceStartDate.isBefore(createdHourPriceEndDate)
        || newHourPriceStartDate.isEqual(createdHourPriceEndDate)
        || newHourPriceStartDate.isEqual(createdHourPriceStartDate);
  }

  private <T extends DateCheckerObject> boolean isEndDateOverlapping(T newPrice, T createdPrice) {
    LocalDate newHourPriceEndDate = newPrice.getEndDate();
    LocalDate createdHourPriceStartDate = createdPrice.getStartDate();
    LocalDate createdHourPriceEndDate = createdPrice.getEndDate();

    return newHourPriceEndDate.isBefore(createdHourPriceEndDate) && newHourPriceEndDate.isAfter(createdHourPriceStartDate)
        || newHourPriceEndDate.isEqual(createdHourPriceStartDate)
        || newHourPriceEndDate.isEqual(createdHourPriceEndDate);
  }

  private <T extends DateCheckerObject> boolean areDatesInBetween(T newPrice, T createdPrice) {
    LocalDate newHourPriceStartDate = newPrice.getStartDate();
    LocalDate newHourPriceEndDate = newPrice.getEndDate();
    LocalDate createdHourPriceStartDate = createdPrice.getStartDate();
    LocalDate createdHourPriceEndDate = createdPrice.getEndDate();

    return newHourPriceStartDate.isBefore(createdHourPriceStartDate) && newHourPriceEndDate.isAfter(createdHourPriceEndDate);
  }

  public <T extends DateCheckerObject> void checkEndDateAfterStartDate(T price) {

    if (!isEndDateAfterOrEqualStartDate(price)) {
      throw new IncorrectDateException("End date must be equal or greater than start date.");
    }

  }

  private <T extends DateCheckerObject> boolean isEndDateAfterOrEqualStartDate(T price) {
    LocalDate startDate = price.getStartDate();
    LocalDate endDate = price.getEndDate();

    return !endDate.isBefore(startDate);
  }

  public <T extends DateCheckerObject> boolean isDateMatching(LocalDate date, T price) {
    LocalDate startDate = price.getStartDate();
    LocalDate endDate = price.getEndDate();

    return date.isEqual(startDate) || date.isEqual(endDate) || date.isAfter(startDate) && date.isBefore(endDate);
  }

  public <T extends DateCheckerObject> boolean areSameDates(T newPrice, T createdPrice) {
    return createdPrice.getStartDate().isEqual(newPrice.getStartDate()) && createdPrice.getEndDate().isEqual(newPrice.getEndDate());
  }
}

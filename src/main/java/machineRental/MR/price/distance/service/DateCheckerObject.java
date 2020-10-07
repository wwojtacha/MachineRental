package machineRental.MR.price.distance.service;

import java.time.LocalDate;
import javax.persistence.Transient;

public class DateCheckerObject {

//  @Transient
  private LocalDate startDate;
//  @Transient
  private LocalDate endDate;

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }
}

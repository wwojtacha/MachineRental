package machineRental.MR.workDocumentEntry;

public enum WorkCode {
  PS("Duration of machine effective work"),
  PX("Duration of machine effective work with auxillary equipment"),
  CP("Time of driving"),
  PP("Duration of paid demurrage"),
  PN("Duration of non-paid demurrage"),
  PZ("Duration of operator`s substitute activity"),
  PR("Duration of employee`s work time not related to operation of the machine");

  private String description;

  WorkCode(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}

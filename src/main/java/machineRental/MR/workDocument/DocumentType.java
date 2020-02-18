package machineRental.MR.workDocument;

public enum DocumentType {
  WORK_REPORT("Work report"),
  ROAD_CARD("Road card");

  private String name;

  DocumentType(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

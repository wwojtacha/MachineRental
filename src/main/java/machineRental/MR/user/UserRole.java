package machineRental.MR.user;

public enum UserRole {

  ADMIN("All functinalities available"),
  USER("Users management prohibited");


  private String description;

  UserRole(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}

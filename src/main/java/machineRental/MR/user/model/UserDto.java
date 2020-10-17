package machineRental.MR.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import machineRental.MR.user.UserRole;

@Data
@AllArgsConstructor
public class UserDto {

  private String username;
  private UserRole userRole;
  private String email;

}

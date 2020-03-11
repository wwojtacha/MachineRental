package machineRental.MR.user.service;

import machineRental.MR.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationValidator {

  boolean isAdmin() {
    return getCurrentUser().isAdmin();
  }

  private User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}

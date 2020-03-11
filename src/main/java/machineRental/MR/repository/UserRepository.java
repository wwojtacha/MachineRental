package machineRental.MR.repository;

import java.util.Optional;
import java.util.UUID;
import machineRental.MR.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

//  Optional<User> findByLogin(String login);

  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);
}

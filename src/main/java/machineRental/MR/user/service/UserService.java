package machineRental.MR.user.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import machineRental.MR.exception.BindingResultException;
import machineRental.MR.exception.NotFoundException;
import machineRental.MR.exception.UnauthorizedException;
import machineRental.MR.repository.UserRepository;
import machineRental.MR.user.UserRole;
import machineRental.MR.user.model.User;
import machineRental.MR.user.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthorizationValidator authorizationValidator;

    public User get(UUID id) {
        if (!authorizationValidator.isAdmin()) {
            throw new UnauthorizedException("You are not permitted to get user information.");
        }
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User with id: %s not found", id)));
    }

    public User create (User user, BindingResult bindingResult) {
        if (!authorizationValidator.isAdmin()) {
            throw new UnauthorizedException("You are not permitted to uploadFile new users.");
        }
        validate(user, null, bindingResult);
        return save(user);
    }

    public User update(User user, UUID id, BindingResult bindingResult) {
        if (!authorizationValidator.isAdmin()) {
            throw new UnauthorizedException("You are not permitted to update user information.");
        }
        validate(user, get(id).getUsername(), bindingResult);

        user.setId(id);
        return save(user);
    }

    private void validate(User user, String currentUsername, BindingResult bindingResult) {
        if (!user.getUsername().equals(currentUsername)
                && userRepository.existsByUsername(user.getUsername())) {
            bindingResult.addError(
                    new FieldError("user", "field",
                            String.format("User with username %s already exists", user.getUsername())));
        }
        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult);
        }
    }

    private User save(User user) {

        String newPassword = user.getPassword();

        if (StringUtils.hasText(newPassword)) {
            user.setPassword(encoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        if (!authorizationValidator.isAdmin()) {
            throw new UnauthorizedException("You are not permitted to get users information.");
        }
        return userRepository.findAll();
    }

    public UserDto getUser(String username) {
        Optional<User> dbUser = userRepository.findByUsername(username);
        if (!dbUser.isPresent()) {
            throw new NotFoundException(String.format("User with username: %s not found.", username));
        }

        User user = dbUser.get();
        UserRole role = user.getRole();
        String email = user.getEmail();
        UserDto userDto = new UserDto(username, role, email);

        return userDto;
    }
}

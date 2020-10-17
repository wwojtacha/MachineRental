package machineRental.MR.user.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import machineRental.MR.user.model.User;
import machineRental.MR.user.model.UserDto;
import machineRental.MR.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User create(@RequestBody @Valid User user, BindingResult bindingResult) {
    return userService.create(user, bindingResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public User update(@PathVariable UUID id, @RequestBody @Valid User user, BindingResult bindingResult) {
    return userService.update(user, id, bindingResult);
  }


  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  public UserDto getUser(@PathVariable String username) {
    return userService.getUser(username);
  }
}

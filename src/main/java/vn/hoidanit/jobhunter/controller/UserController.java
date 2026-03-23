package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/create")
    public String getCreateNewUser() {
        User user = new User();
        user.setEmail("1@gmail.com");
        user.setName("rin");
        user.setPassword("1234");
        this.userService.handleCreateUser(user);
        return "create user";
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(
            @RequestBody User postManUser) {
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id > 1500) {
            throw new IdInvalidException("Loi roi th l!!!");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("delete");
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(updatedUser);

    }
}

package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

@RestController
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/create")
    public String getCreateNewUser(){
        User user = new User();
        user.setEmail("1@gmail.com");
        user.setName("rin");
        user.setPassword("1234");
        this.userService.handleCreateUser(user);
        return "create user";
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id){
        User user =  this.userService.fetchUserById(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser(){
        List<User> listUser = this.userService.fetchAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(listUser);
    }
    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(
        @RequestBody User postManUser){
        User newUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id){
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("delete");
    }
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        User updatedUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.ok(updatedUser);
        
    }
}

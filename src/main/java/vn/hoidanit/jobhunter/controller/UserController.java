package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/user/create")
    public String getCreateNewUser(){
        User user = new User();
        user.setEmail("1@gmail.com");
        user.setName("rin");
        user.setPassword("1234");
        this.userService.handleCreateUser(user);
        return "create user";
    }
    @PostMapping("/user/create")
    public User createNewUser(
        @RequestBody User postManUser){
        User newUser = this.userService.handleCreateUser(postManUser);
        return newUser;
    }
}

package com.Sridevi.Campushelpdesk.Controller;

import com.Sridevi.Campushelpdesk.DTO.AuthResponse;
import com.Sridevi.Campushelpdesk.DTO.UserRequest;
import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
   private UserService userservice;
    @PostMapping("/register")
    public UserResponse register(@RequestBody User user)
    {
         UserResponse userResponse = userservice.register(user);
         return userResponse;
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserRequest userRequest){
        return userservice.login(userRequest);
    }
}

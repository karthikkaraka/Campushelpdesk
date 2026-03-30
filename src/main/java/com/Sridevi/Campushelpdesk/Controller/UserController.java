package com.Sridevi.Campushelpdesk.Controller;

import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
   private UserService userservice;
    @PostMapping("/register")
    public UserResponse register(User user)
    {
         UserResponse userResponse = userservice.register(user);
         return null;
    }
}

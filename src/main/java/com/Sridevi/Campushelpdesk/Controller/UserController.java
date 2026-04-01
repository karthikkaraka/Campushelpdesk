package com.Sridevi.Campushelpdesk.Controller;

import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
   private UserService userservice;
    @PostMapping("/otpverify")
    public ResponseEntity<UserResponse> otpVerification(@RequestBody OtpVerification otpVer) {
        UserResponse userResponse = userser.otpVerify(otpVer);
        if (userResponse == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(User user)
    {
        userservice.register(user);
         return new ResponseEntity<>("Otp semt to Email", HttpStatus.OK);
    }
}

package com.Sridevi.Campushelpdesk.Controller;

import com.Sridevi.Campushelpdesk.DTO.*;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userservice;

    @PostMapping("/otpverify")
    public ResponseEntity<UserResponse> otpVerification(@RequestBody OtpVerification otpVer) {
        UserResponse userResponse = userservice.otpVerify(otpVer); // ✅ fixed typo
        if (userResponse == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        userservice.register(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserRequest userRequest) {
        return userservice.login(userRequest);
    }
}
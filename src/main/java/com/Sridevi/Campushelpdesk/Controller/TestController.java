package com.Sridevi.Campushelpdesk.Controller;

import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final UserRepository repository;
    @GetMapping
    public String test(){
        return "JWT Working ✅";
    }
    @GetMapping("/users")
    public String userTesting(){
        return "User API Working";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminTesting(){
        return "Admin API Working";
    }

    @GetMapping("/me")
        public UserResponse getLoggedInUser(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User user=repository.findByEmail(auth.getName()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
        return UserResponse.builder()
                .userId(user.getUserId())
                .regNo(user.getRegno())
                .email(user.getEmail())
                .userName(user.getUsername())
                .accountStatus(user.getAccountStatus())
                .build();
    }
}

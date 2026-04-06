package com.Sridevi.Campushelpdesk.Service;

import com.Sridevi.Campushelpdesk.Config.JwtService;
import com.Sridevi.Campushelpdesk.DTO.AuthResponse;
import com.Sridevi.Campushelpdesk.DTO.UserRequest;
import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Enums.AccountStatus;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
private final UserRepository userRepository;
private final PasswordEncoder encoder;
private final JwtService jwtService;
    public UserResponse register(User user) {
          return null;
    }

    public AuthResponse login(UserRequest userRequest) {

        User user=userRepository.findByEmail(userRequest.getEmail()).orElseThrow(()->new UsernameNotFoundException("UserNotFound"));
        if(! user.isEmailVerified()){
throw  new BadCredentialsException("Please verify your email first");
        }
        if(user.getAccountStatus().equals(AccountStatus.LOCKED)){
            throw  new BadCredentialsException("Account Locked");
        }
        if( ! encoder.matches(userRequest.getPassword(),user.getPassword())){
            throw  new BadCredentialsException("Invalid credentials");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("user_id",user.getUserId());
        map.put("role",user.getRole());
        map.put("email",user.getEmail());
        return AuthResponse .builder().token(
                jwtService.generateToken(map,user))
                .message("Login Successful")
                .build();

    }
}

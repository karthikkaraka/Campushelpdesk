package com.Sridevi.Campushelpdesk.Service;

import com.Sridevi.Campushelpdesk.Config.JwtService;
import com.Sridevi.Campushelpdesk.DTO.*;
import com.Sridevi.Campushelpdesk.Enums.AccountStatus;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.Sridevi.Campushelpdesk.DTO.UserResponse;
import com.Sridevi.Campushelpdesk.Enums.AccountStatus;
import com.Sridevi.Campushelpdesk.Enums.UserRole;
import com.Sridevi.Campushelpdesk.Model.User;
import com.Sridevi.Campushelpdesk.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
private final UserRepository userRepository;
private final PasswordEncoder encoder;
private final JwtService jwtService;
    @Autowired
    private UserRepository userRepo;
   @Autowired
   public JavaMailSender mailsender;

    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int otp =  100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    public void register(RegisterRequest request) {

        if (request.getRegno() == null || request.getRegno().isEmpty()) {
            throw new RuntimeException("RegNo is required");
        }

        String mail = request.getEmail();
        User existingUser = userRepo.findByEmail(mail).orElse(null);

        if (existingUser != null) {
            if (existingUser.getAccountStatus() == AccountStatus.ACTIVE) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
            }

            if (existingUser.getEmaiOtpExpiry().isBefore(LocalDateTime.now())) {
                String newOtp = generateOTP();
                existingUser.setEmailOtp(newOtp);
                existingUser.setEmaiOtpExpiry(LocalDateTime.now().plusMinutes(10));
                userRepo.save(existingUser);
                sendOTPMail(newOtp, existingUser.getEmail());
            } else {
                sendOTPMail(existingUser.getEmailOtp(), existingUser.getEmail());
            }

            return;
        }

        // ✅ Build entity safely
        User user = new User();
        String otp = generateOTP();

        user.setEmail(request.getEmail());
        user.setRegno(request.getRegno());
        user.setUserName(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmailOtp(otp);
        user.setEmaiOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setAccountStatus(AccountStatus.PENDING);
        user.setRole(UserRole.USER); // ✅ FIXED ROLE
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepo.save(user);
        sendOTPMail(otp, user.getEmail());
    }
    public void sendOTPMail(String otp,String mail){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mail);
        msg.setSubject("OTP mail");
        msg.setText(otp);
        mailsender.send(msg);
    }
    public UserResponse otpVerify(OtpVerification otpVer) {

        User user = userRepo.findByEmail(otpVer.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // ✅ Already verified check
        if (user.isEmailVerified()) {
            throw new RuntimeException("User already verified");
        }

        // ✅ Null safety
        if (user.getEmailOtp() == null || user.getEmaiOtpExpiry() == null) {
            throw new RuntimeException("OTP not generated or already used");
        }

        // ✅ Expiry check FIRST
        if (user.getEmaiOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired!!");
        }

        // ✅ OTP match check
        if (!otpVer.getOtp().equals(user.getEmailOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // ✅ Success
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());
        user.setEmailVerified(true);
        user.setEmailOtp(null);
        user.setEmaiOtpExpiry(null);

        userRepo.save(user);

        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRegNo(user.getRegno());
        response.setAccountStatus(user.getAccountStatus());

        return response;
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

package com.Sridevi.Campushelpdesk.Service;

import com.Sridevi.Campushelpdesk.DTO.OtpVerification;
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

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
   @Autowired
   public JavaMailSender mailsender;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(14);
    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int otp =  100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    public void register(User user) {
        String mail = user.getEmail();
        User existingUser = userRepo.findByEmail(mail);
        if(existingUser!=null)
        {
            if(existingUser.getAccountStatus() == AccountStatus.ACTIVE){
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "User already exists"
                );
            }
            if(existingUser.getEmaiOtpExpiry().isBefore(LocalDateTime.now())){
                String newOtp = generateOTP();
                existingUser.setEmailOtp(newOtp);
                existingUser.setEmaiOtpExpiry(LocalDateTime.now().plusMinutes(5));
                try{
                    userRepo.save(existingUser);
                }
                catch(DataIntegrityViolationException e) {
                    throw new RuntimeException("email or reg no already exists");
                }
                sendOTPMail(newOtp,existingUser.getEmail());
            }
             /*
             if user registers and otp is sent and user register again in btw 5 minutes....so we should not create another record in database
             but send the same email with same otp...
              */
            sendOTPMail(existingUser.getEmailOtp(), existingUser.getEmail());
        }
          User userr = new User();
         String otp = generateOTP();
          userr.setEmail(user.getEmail());
          userr.setRegno(user.getRegno());
          userr.setEmailOtp(otp);
          userr.setPassword(encoder.encode(user.getPassword()));
          userr.setAccountStatus(AccountStatus.PENDING);
          userr.setRole(UserRole.USER);
          userr.setEmaiOtpExpiry(LocalDateTime.now().plusMinutes(5));
          userr.setUserName(user.getUserName());
          userr.setCreatedAt(LocalDateTime.now());
          userr.setUpdatedAt(LocalDateTime.now());
          userr.setEmailVerified(false);
          userRepo.save(userr);
    }
    public void sendOTPMail(String otp,String mail){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mail);
        msg.setSubject("OTP mail");
        msg.setText(otp);
        mailsender.send(msg);
    }
    public UserResponse otpVerify(OtpVerification otpVer){
        User user = userRepo.findByEmail(otpVer.getEmail());
        if(user == null){
            return null;
        }
        String otp = otpVer.getOtp();
        if(otp.equals(user.getEmailOtp())){
            if(user.getEmaiOtpExpiry().isBefore(LocalDateTime.now()))
                throw new RuntimeException("OTP Expired!!");
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setUpdatedAt(LocalDateTime.now());
            user.setEmailVerified(true);
            user.setEmailOtp(null);
            user.setEmaiOtpExpiry(null);
            userRepo.save(user);
        }
        else{
            return null;
        }
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRegNo(user.getRegno());
        response.setAccountStatus(user.getAccountStatus());
        return response;
    }
}

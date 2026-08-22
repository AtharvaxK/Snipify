package com.snipify.snipify.service;

import com.snipify.snipify.CustomExceptions.OtpAttemptsOver;
import com.snipify.snipify.CustomExceptions.OtpExpiredException;
import com.snipify.snipify.CustomExceptions.ResetPasswordWindowExpiredException;
import com.snipify.snipify.CustomExceptions.WrongOtpException;
import com.snipify.snipify.dto.*;
import com.snipify.snipify.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public ResetPasswordOtpResponse forgotPassword(ResetPasswordEmail resetPasswordEmail){
        boolean userExist=userRepository.existsByEmail(resetPasswordEmail.getEmail());

        if (userExist){
            try {
                SecureRandom secureRandom = new SecureRandom();

                int otp = secureRandom.nextInt(1000000);

                String generatedString = String.format("%06d", otp);

                String keyForEmail = "otp:" + resetPasswordEmail.getEmail();
                String keyForAttempts = "otp:attempts:" + resetPasswordEmail.getEmail();
                String encodedOtp=passwordEncoder.encode(generatedString);

                if(encodedOtp!=null) {
                    redisTemplate.opsForValue().set(keyForEmail, encodedOtp, Duration.ofSeconds(300));
                    redisTemplate.opsForValue().set(keyForAttempts, 5, Duration.ofSeconds(300));
                }
                ResetPasswordOtpResponse response=new ResetPasswordOtpResponse();
                response.setEmail(resetPasswordEmail.getEmail());
                response.setOtp(generatedString);
                return response;

            }
            catch (Exception e) {
                log.error("Failed to generate and store OTP for email: {}", resetPasswordEmail.getEmail(), e);
                throw new RuntimeException("Could not generate OTP. Please try again.");
            }

        }
        return null;
    }

    public OtpResetTokenDto verifyOtp(VerifyOtpRequestDto verifyOtpDto){
        String email= verifyOtpDto.getEmail();
        String otpFromRedis= (String) redisTemplate.opsForValue().get("otp:"+email);
        Object attempt= redisTemplate.opsForValue().get("otp:attempts:"+email);

        if (otpFromRedis==null){
            throw new OtpExpiredException("OTP has expired or is invalid. Please request a new one.");
        }
        else if (attempt==null) {
            throw new OtpExpiredException("OTP has expired or is invalid. Please request a new one.");
        } else if (Integer.parseInt(attempt.toString())<=0) {
            redisTemplate.delete(Set.of("otp:"+email,"otp:attempts:"+email));
            throw new OtpAttemptsOver("Maximum OTP attempts exceeded.Please request a new OTP.");
        }
        String OtpSent=verifyOtpDto.getOtp();
        if(OtpSent!=null&&!OtpSent.isBlank()){
            if(passwordEncoder.matches(OtpSent,otpFromRedis)){
                redisTemplate.delete(Set.of("otp:"+email,"otp:attempts:"+email));

                String resetToken=UUID.randomUUID().toString();
                redisTemplate.opsForValue().set("reset_token:"+email,resetToken,Duration.ofSeconds(300));
                OtpResetTokenDto otpResetTokenDto=new OtpResetTokenDto();
                otpResetTokenDto.setResetToken(resetToken);
                return otpResetTokenDto;
            }
            else {
                Long chances =redisTemplate.opsForValue().decrement("otp:attempts:"+email,1);
                throw new WrongOtpException("Wrong OTP entered attempts remaining : "+chances);
            }
        }
        throw new BadCredentialsException("Something Went wrong!");
    }

    @Transactional
    public String resetPassword(ResetNewPasswordDto resetNewPasswordDto){
        String email= resetNewPasswordDto.getEmail();
        String resetToken= resetNewPasswordDto.getResetToken();
        String resetTokenFromRedis= (String) redisTemplate.opsForValue().get("reset_token:"+email);

        if (resetTokenFromRedis==null||resetTokenFromRedis.isBlank()){
            throw new ResetPasswordWindowExpiredException("Window to reset password expired. Try again!");
        }
        else if(resetToken!=null &&resetToken.equals(resetTokenFromRedis)){
            redisTemplate.delete("reset_token:"+email);
            userRepository.updatePasswordByEmail(email,passwordEncoder.encode(resetNewPasswordDto.getNewPassword()));
            return "Password reset completed successfully";
        }
        throw new BadCredentialsException("Invalid reset Token");
    }
}

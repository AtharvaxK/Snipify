package com.snipify.snipify.service;

import com.snipify.snipify.dto.ResetPasswordEmail;
import com.snipify.snipify.dto.VerifyOtpDto;
import com.snipify.snipify.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public String forgotPassword(ResetPasswordEmail resetPasswordEmail){
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
                return generatedString;

            }
            catch (Exception e) {
                log.error("Failed to generate and store OTP for email: {}", resetPasswordEmail.getEmail(), e);
                throw new RuntimeException("Could not generate OTP. Please try again.");
            }

        }
        return null;
    }

    public void verifyOtp(VerifyOtpDto verifyOtpDto){

    }
}

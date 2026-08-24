package com.snipify.snipify.controller;

import com.snipify.snipify.CustomExceptions.SubdomainExistsException;
import com.snipify.snipify.CustomExceptions.UserNameExsitsException;
import com.snipify.snipify.dto.*;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.ProRepository;
import com.snipify.snipify.repo.UserRepository;
import com.snipify.snipify.security.AuthService;
import com.snipify.snipify.service.EmailService;
import com.snipify.snipify.service.ForgotPasswordService;
import com.snipify.snipify.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;
    @Value("${app.security.cookie-secure}")
    private boolean isCookieSecure;

    private final UserRepository userRepository;
    private final ProRepository proRepository;

    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto>signup(@RequestBody SignupRequestDto signupRequestDto){
        SignupResponseDto signupResponseDto=authService.signup(signupRequestDto);

        if(signupRequestDto==null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(signupResponseDto,HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        try {
            String jwt=authService.login(loginRequestDto);
            ResponseCookie responseCookie=ResponseCookie.from("jwt",jwt).
                    httpOnly(true)
                    .secure(isCookieSecure)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());
            return new ResponseEntity<>("Login Successful!",HttpStatus.OK);
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> sendOtp(@RequestBody ResetPasswordEmail resetPasswordEmail){
        String s=forgotPasswordService.forgotPassword(resetPasswordEmail);

        return new ResponseEntity<>(s,HttpStatus.OK);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDto verifyOtpDto){

        OtpResetTokenDto otpResetTokenDto=forgotPasswordService.verifyOtp(verifyOtpDto);
        ResponseCookie resetCookie = ResponseCookie.from("reset_token",otpResetTokenDto.getResetToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/auth/resetPassword")
                .maxAge(300)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resetCookie.toString())
                .body("OTP verified successfully. You may now reset your password.");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetNewPasswordDto resetNewPasswordDto,
                                                @CookieValue(name = "reset_token", required = false) String resetTokenFromCookie) {
        String resultMessage = forgotPasswordService.resetPassword(resetNewPasswordDto,resetTokenFromCookie);
        ResponseCookie deleteCookie = ResponseCookie.from("reset_token", "")
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/auth/resetPassword")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(resultMessage);
    }

    @PostMapping("/onboard")
    public ResponseEntity<?> onboard(@RequestBody NewUserSignupForm newUserSignupForm , @AuthenticationPrincipal UserDetailsImpl userDetails){
        try {
            int a=authService.onboard(newUserSignupForm, userDetails.getUser());
        }
        catch (UserNameExsitsException | SubdomainExistsException e){
            return new ResponseEntity<>(e,HttpStatus.NOT_ACCEPTABLE);
        }


        return new ResponseEntity<>("Onboarding completed!",HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userDetails.getUser();
        boolean needsOnboarding = user.getPro() == null || user.getPro().getSubdomainName() == null;
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("subdomain", user.getPro() != null ? user.getPro().getSubdomainName() : null);
        result.put("roles", user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toList()));
        result.put("needsOnboarding", needsOnboarding);
        result.put("providerType", user.getProviderTypes() != null ? user.getProviderTypes().name() : "EMAIL");
        return ResponseEntity.ok(result);
    }

}

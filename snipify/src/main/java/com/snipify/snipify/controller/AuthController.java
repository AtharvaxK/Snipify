package com.snipify.snipify.controller;

import com.snipify.snipify.dto.*;
import com.snipify.snipify.security.AuthService;
import com.snipify.snipify.service.ForgotPasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto>signup(@RequestBody SignupRequestDto signupRequestDto){
        SignupResponseDto signupResponseDto=authService.signup(signupRequestDto);

        if(signupRequestDto==null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(signupResponseDto,HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        try {
            LoginResponseDto loginResponseDto=authService.login(loginRequestDto);

            return new ResponseEntity<>(loginResponseDto,HttpStatus.OK);
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<ResetPasswordOtpResponse> sendOtp(@RequestBody ResetPasswordEmail resetPasswordEmail){
        ResetPasswordOtpResponse response=forgotPasswordService.forgotPassword(resetPasswordEmail);

        if (response==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<OtpResetTokenDto> verifyOtp(@RequestBody VerifyOtpRequestDto verifyOtpDto){

        OtpResetTokenDto otpResetTokenDto=forgotPasswordService.verifyOtp(verifyOtpDto);
        return new ResponseEntity<>(otpResetTokenDto,HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetNewPasswordDto resetNewPasswordDto) {
        String resultMessage = forgotPasswordService.resetPassword(resetNewPasswordDto);
        return ResponseEntity.ok(resultMessage);
    }


}

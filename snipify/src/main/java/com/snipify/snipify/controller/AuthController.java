package com.snipify.snipify.controller;

import com.snipify.snipify.dto.LoginRequestDto;
import com.snipify.snipify.dto.LoginResponseDto;
import com.snipify.snipify.dto.SignupRequestDto;
import com.snipify.snipify.dto.SignupResponseDto;
import com.snipify.snipify.security.AuthService;
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

    private AuthService authService;

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
}

package com.snipify.snipify.controller;

import com.snipify.snipify.dto.ApiKeyResponseDto;
import com.snipify.snipify.model.User;
import com.snipify.snipify.service.ApiService;
import com.snipify.snipify.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    @PostMapping("/generate-api-key")
    public ResponseEntity<ApiKeyResponseDto> generateApiKey(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest httpServletRequest){


        if (userDetails!=null) {
            ApiKeyResponseDto apiKeyResponseDto=apiService.generateApi_key(userDetails.getUser());
            return new ResponseEntity<>(apiKeyResponseDto,HttpStatus.OK);
        }


        return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
    }
}

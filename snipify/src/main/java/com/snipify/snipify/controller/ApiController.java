package com.snipify.snipify.controller;

import com.snipify.snipify.dto.ApiKeyResponseDto;
import com.snipify.snipify.service.ApiService;
import com.snipify.snipify.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    public ResponseEntity<ApiKeyResponseDto> generateApiKey(@AuthenticationPrincipal UserDetailsImpl userDetails){
        ApiKeyResponseDto apiKeyResponseDto=new ApiKeyResponseDto();

        return new ResponseEntity<>(apiKeyResponseDto, HttpStatus.OK);
    }
}

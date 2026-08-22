package com.snipify.snipify.service;

import com.snipify.snipify.Enums.Status;
import com.snipify.snipify.dto.ApiKeyResponseDto;
import com.snipify.snipify.model.API_User;
import com.snipify.snipify.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiService {

    public ApiKeyResponseDto generateApi_key(User authenticatedUser)  {

        String apiKey="snipify/"+new Base64StringKeyGenerator(32).generateKey();
        API_User apiUser=new API_User();
        apiUser.setUser(authenticatedUser);
        apiUser.setStatus(Status.ACTIVE);
        apiUser.setCreatedAt(LocalDateTime.now());
        authenticatedUser.setApiUser(apiUser);
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(apiKey.getBytes(StandardCharsets.UTF_8));

          apiUser.setApiKey(HexFormat.of().formatHex(hash));
        }
        catch (NoSuchAlgorithmException e){
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
        ApiKeyResponseDto apiKeyResponseDto=new ApiKeyResponseDto();
        apiKeyResponseDto.setApiKey(apiKey);

        return apiKeyResponseDto;
    }
}

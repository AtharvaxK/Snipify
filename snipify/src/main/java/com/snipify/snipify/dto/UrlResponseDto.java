package com.snipify.snipify.dto;

import com.snipify.snipify.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UrlResponseDto {
    private Long urlId;
    private String shortCode;
    private String longUrl;
    long clickCount;
    Status status;
    LocalDateTime expiryTime;
}

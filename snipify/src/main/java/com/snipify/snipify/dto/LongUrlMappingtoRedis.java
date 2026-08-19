package com.snipify.snipify.dto;

import com.snipify.snipify.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class LongUrlMappingtoRedis implements Serializable {
    private Long id;
    private String shortcode;
    private String longUrl;
    private Status status;
    private LocalDateTime expiryTime;
    private Long clickCount;
    private boolean isPremium;
}

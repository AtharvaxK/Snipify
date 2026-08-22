package com.snipify.snipify.dto;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {

    private String email;
    private String otp;
}

package com.snipify.snipify.dto;

import lombok.Data;

@Data
public class ResetPasswordOtpResponse {
    private String email;
    private String otp;
}

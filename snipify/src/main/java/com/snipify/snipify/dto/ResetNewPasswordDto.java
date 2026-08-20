package com.snipify.snipify.dto;

import lombok.Data;

@Data
public class ResetNewPasswordDto {

    private String email;
    private String resetToken;
    private String newPassword;
}

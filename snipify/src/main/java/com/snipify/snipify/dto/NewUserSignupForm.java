package com.snipify.snipify.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewUserSignupForm {
    private String userName;
    private String subdomain;

}

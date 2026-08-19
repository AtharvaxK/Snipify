package com.snipify.snipify.dto;

import com.snipify.snipify.Enums.Roles;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequestDto {
    private String username;
    private String password;
    private String email;
    private Set<Roles>role;
    private String subDomain;
}

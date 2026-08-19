package com.snipify.snipify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDto {

    private long Id;
    private String username;
    private String email;
    private String subDomain;
}

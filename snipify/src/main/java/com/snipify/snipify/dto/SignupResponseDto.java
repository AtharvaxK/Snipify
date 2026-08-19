package com.snipify.snipify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String username;
}

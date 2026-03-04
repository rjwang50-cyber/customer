package com.example.crm.dto.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialAccountDto {
    @NotBlank
    private String platform;
    @NotBlank
    private String account;
}

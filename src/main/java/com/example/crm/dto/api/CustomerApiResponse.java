package com.example.crm.dto.api;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerApiResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDate birthday;
    private String preferredLanguage;
    private String hobbies;
    private String notes;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SocialAccountDto> socialAccounts = new ArrayList<>();
}

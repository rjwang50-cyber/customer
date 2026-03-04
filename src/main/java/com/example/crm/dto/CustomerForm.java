package com.example.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerForm {
    private Long id;

    @NotBlank
    private String name;

    @Pattern(regexp = "^[0-9+\\- ]{6,20}$", message = "phone format invalid")
    private String phone;

    @Email
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String hobbies;
    private String notes;
    private String preferredLanguage = "en";

    private List<String> socialPlatforms = new ArrayList<>();
    private List<String> socialAccounts = new ArrayList<>();
}

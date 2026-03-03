package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlogPostForm {
    private Long id;

    @NotBlank
    private String title;

    private String summary;

    @NotBlank
    private String content;

    private String visibility;
}

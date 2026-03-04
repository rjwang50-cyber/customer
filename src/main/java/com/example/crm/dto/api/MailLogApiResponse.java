package com.example.crm.dto.api;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MailLogApiResponse {
    private Long id;
    private Long customerId;
    private String sendType;
    private String holidayCode;
    private LocalDate targetDate;
    private String toEmail;
    private String subject;
    private String status;
    private String errorMsg;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}

package com.example.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("mail_send_log")
public class MailSendLog {
    @TableId(type = IdType.AUTO)
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

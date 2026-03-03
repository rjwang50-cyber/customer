package com.example.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_social_account")
public class CustomerSocialAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String platform;
    private String account;
    private LocalDateTime createdAt;
}

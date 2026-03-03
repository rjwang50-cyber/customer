package com.example.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("blog_post")
public class BlogPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ownerEmployeeId;
    private String title;
    private String summary;
    private String content;
    private String status;
    private String visibility;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

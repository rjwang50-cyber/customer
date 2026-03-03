package com.example.crm.service;

import com.example.crm.dto.BlogPostForm;
import com.example.crm.entity.BlogPost;

import java.util.List;

public interface BlogService {
    List<BlogPost> publicPosts();
    List<BlogPost> postsByEmployee(Long employeeId);
    BlogPost findById(Long id);
    void saveDraft(BlogPostForm form, Long employeeId);
    void publish(Long id, Long employeeId);
}

package com.example.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.crm.dto.BlogPostForm;
import com.example.crm.entity.BlogPost;

import java.util.List;

public interface BlogService {
    IPage<BlogPost> publicPostsPage(long pageNo, long pageSize);
    List<BlogPost> publicPosts();
    IPage<BlogPost> postsByEmployeePage(Long employeeId, long pageNo, long pageSize);
    List<BlogPost> postsByEmployee(Long employeeId);
    BlogPost findById(Long id);
    void saveDraft(BlogPostForm form, Long employeeId);
    void publish(Long id, Long employeeId);
}

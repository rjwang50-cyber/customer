package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.dto.BlogPostForm;
import com.example.crm.entity.BlogPost;
import com.example.crm.mapper.BlogPostMapper;
import com.example.crm.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostMapper blogPostMapper;

    @Override
    public List<BlogPost> publicPosts() {
        return blogPostMapper.selectList(new LambdaQueryWrapper<BlogPost>()
                .eq(BlogPost::getStatus, "published")
                .eq(BlogPost::getVisibility, "public")
                .orderByDesc(BlogPost::getPublishedAt));
    }

    @Override
    public List<BlogPost> postsByEmployee(Long employeeId) {
        return blogPostMapper.selectList(new LambdaQueryWrapper<BlogPost>()
                .eq(BlogPost::getOwnerEmployeeId, employeeId)
                .orderByDesc(BlogPost::getUpdatedAt));
    }

    @Override
    public BlogPost findById(Long id) {
        return blogPostMapper.selectById(id);
    }

    @Override
    public void saveDraft(BlogPostForm form, Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        BlogPost post;
        if (form.getId() == null) {
            post = new BlogPost();
            post.setOwnerEmployeeId(employeeId);
            post.setCreatedAt(now);
            post.setStatus("draft");
        } else {
            post = blogPostMapper.selectById(form.getId());
            if (post == null || !post.getOwnerEmployeeId().equals(employeeId)) {
                throw new IllegalArgumentException("Post not found");
            }
        }
        post.setTitle(form.getTitle());
        post.setSummary(form.getSummary());
        post.setContent(form.getContent());
        post.setVisibility(form.getVisibility() == null ? "public" : form.getVisibility());
        post.setUpdatedAt(now);
        if (form.getId() == null) {
            blogPostMapper.insert(post);
        } else {
            blogPostMapper.updateById(post);
        }
    }

    @Override
    public void publish(Long id, Long employeeId) {
        BlogPost post = blogPostMapper.selectById(id);
        if (post == null || !post.getOwnerEmployeeId().equals(employeeId)) {
            throw new IllegalArgumentException("Post not found");
        }
        post.setStatus("published");
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        blogPostMapper.updateById(post);
    }
}

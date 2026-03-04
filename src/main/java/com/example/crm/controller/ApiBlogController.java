package com.example.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.crm.dto.BlogPostForm;
import com.example.crm.dto.api.ApiPageResponse;
import com.example.crm.entity.BlogPost;
import com.example.crm.service.BlogService;
import com.example.crm.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiBlogController {

    private final BlogService blogService;
    private final UserContextService userContextService;

    @GetMapping("/blog/posts")
    public ApiPageResponse<BlogPost> publicPosts(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size) {
        IPage<BlogPost> postPage = blogService.publicPostsPage(page, size);
        return ApiPageResponse.from(postPage);
    }

    @GetMapping("/blog/posts/{id}")
    public BlogPost publicPost(@PathVariable Long id) {
        BlogPost post = blogService.findById(id);
        if (post == null || !"published".equals(post.getStatus()) || !"public".equals(post.getVisibility())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    @GetMapping("/me/blog/posts")
    public ApiPageResponse<BlogPost> myPosts(@RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size) {
        Long employeeId = userContextService.currentEmployeeId();
        IPage<BlogPost> postPage = blogService.postsByEmployeePage(employeeId, page, size);
        return ApiPageResponse.from(postPage);
    }

    @PostMapping("/me/blog/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveDraft(@Valid @RequestBody BlogPostForm form) {
        blogService.saveDraft(form, userContextService.currentEmployeeId());
        return "ok";
    }

    @PostMapping("/me/blog/posts/{id}/publish")
    public String publish(@PathVariable Long id) {
        blogService.publish(id, userContextService.currentEmployeeId());
        return "ok";
    }
}

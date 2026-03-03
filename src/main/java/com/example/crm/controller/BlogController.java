package com.example.crm.controller;

import com.example.crm.dto.BlogPostForm;
import com.example.crm.entity.BlogPost;
import com.example.crm.service.BlogService;
import com.example.crm.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class BlogController {

    private final BlogService blogService;
    private final UserContextService userContextService;

    @GetMapping("/blog")
    public String publicBlogList(Model model) {
        model.addAttribute("posts", blogService.publicPosts());
        return "blog/public-list";
    }

    @GetMapping("/blog/{id}")
    public String publicBlogDetail(@PathVariable Long id, Model model) {
        BlogPost post = blogService.findById(id);
        model.addAttribute("post", post);
        return "blog/public-detail";
    }

    @GetMapping("/me/blog")
    public String myBlog(Model model) {
        Long employeeId = userContextService.currentEmployeeId();
        model.addAttribute("posts", blogService.postsByEmployee(employeeId));
        model.addAttribute("blogForm", new BlogPostForm());
        return "blog/me-blog";
    }

    @PostMapping("/me/blog")
    public String saveMyPost(@Valid BlogPostForm blogPostForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("posts", blogService.postsByEmployee(userContextService.currentEmployeeId()));
            return "blog/me-blog";
        }
        blogService.saveDraft(blogPostForm, userContextService.currentEmployeeId());
        return "redirect:/me/blog";
    }

    @PostMapping("/me/blog/{id}/publish")
    public String publish(@PathVariable Long id) {
        blogService.publish(id, userContextService.currentEmployeeId());
        return "redirect:/me/blog";
    }
}

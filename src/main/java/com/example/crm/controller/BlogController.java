package com.example.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class BlogController {

    private final BlogService blogService;
    private final UserContextService userContextService;

    @GetMapping("/blog")
    public String publicBlogList(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "9") long size,
                                 Model model) {
        IPage<BlogPost> postPage = blogService.publicPostsPage(page, size);
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("currentPage", postPage.getCurrent());
        model.addAttribute("totalPages", postPage.getPages());
        model.addAttribute("hasPrevious", postPage.getCurrent() > 1);
        model.addAttribute("hasNext", postPage.getCurrent() < postPage.getPages());
        model.addAttribute("size", size);
        return "blog/public-list";
    }

    @GetMapping("/blog/{id}")
    public String publicBlogDetail(@PathVariable Long id, Model model) {
        BlogPost post = blogService.findById(id);
        model.addAttribute("post", post);
        return "blog/public-detail";
    }

    @GetMapping("/me/blog")
    public String myBlog(@RequestParam(defaultValue = "1") long page,
                         @RequestParam(defaultValue = "10") long size,
                         Model model) {
        Long employeeId = userContextService.currentEmployeeId();
        IPage<BlogPost> postPage = blogService.postsByEmployeePage(employeeId, page, size);
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("currentPage", postPage.getCurrent());
        model.addAttribute("totalPages", postPage.getPages());
        model.addAttribute("hasPrevious", postPage.getCurrent() > 1);
        model.addAttribute("hasNext", postPage.getCurrent() < postPage.getPages());
        model.addAttribute("size", size);
        model.addAttribute("blogForm", new BlogPostForm());
        return "blog/me-blog";
    }

    @PostMapping("/me/blog")
    public String saveMyPost(@Valid BlogPostForm blogPostForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            IPage<BlogPost> postPage = blogService.postsByEmployeePage(userContextService.currentEmployeeId(), 1, 10);
            model.addAttribute("posts", postPage.getRecords());
            model.addAttribute("currentPage", postPage.getCurrent());
            model.addAttribute("totalPages", postPage.getPages());
            model.addAttribute("hasPrevious", false);
            model.addAttribute("hasNext", postPage.getCurrent() < postPage.getPages());
            model.addAttribute("size", 10);
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

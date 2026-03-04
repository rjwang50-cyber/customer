package com.example.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.entity.MailSendLog;
import com.example.crm.mapper.MailSendLogMapper;
import com.example.crm.service.GreetingMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailSendLogMapper mailSendLogMapper;
    private final GreetingMailService greetingMailService;

    @GetMapping("/logs")
    public String logs(@RequestParam(defaultValue = "1") long page,
                       @RequestParam(defaultValue = "20") long size,
                       Model model) {
        IPage<MailSendLog> logPage = mailSendLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<MailSendLog>().orderByDesc(MailSendLog::getCreatedAt));
        model.addAttribute("logs", logPage.getRecords());
        model.addAttribute("currentPage", logPage.getCurrent());
        model.addAttribute("totalPages", logPage.getPages());
        model.addAttribute("hasPrevious", logPage.getCurrent() > 1);
        model.addAttribute("hasNext", logPage.getCurrent() < logPage.getPages());
        model.addAttribute("size", size);
        return "mail/logs";
    }

    @PostMapping("/run-now")
    public String runNow() {
        greetingMailService.runDailyJob();
        return "redirect:/mail/logs";
    }
}

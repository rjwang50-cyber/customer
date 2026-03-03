package com.example.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.MailSendLog;
import com.example.crm.mapper.MailSendLogMapper;
import com.example.crm.service.GreetingMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailSendLogMapper mailSendLogMapper;
    private final GreetingMailService greetingMailService;

    @GetMapping("/logs")
    public String logs(Model model) {
        model.addAttribute("logs", mailSendLogMapper.selectList(new LambdaQueryWrapper<MailSendLog>()
                .orderByDesc(MailSendLog::getCreatedAt)
                .last("limit 200")));
        return "mail/logs";
    }

    @PostMapping("/run-now")
    public String runNow() {
        greetingMailService.runDailyJob();
        return "redirect:/mail/logs";
    }
}

package com.example.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crm.dto.api.ApiPageResponse;
import com.example.crm.dto.api.MailLogApiResponse;
import com.example.crm.entity.MailSendLog;
import com.example.crm.mapper.MailSendLogMapper;
import com.example.crm.service.GreetingMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class ApiMailController {

    private final MailSendLogMapper mailSendLogMapper;
    private final GreetingMailService greetingMailService;

    @GetMapping("/logs")
    public ApiPageResponse<MailLogApiResponse> logs(@RequestParam(defaultValue = "1") long page,
                                                    @RequestParam(defaultValue = "20") long size) {
        IPage<MailSendLog> logPage = mailSendLogMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<MailSendLog>().orderByDesc(MailSendLog::getCreatedAt));
        List<MailLogApiResponse> items = logPage.getRecords().stream().map(this::toResponse).toList();
        ApiPageResponse<MailLogApiResponse> response = new ApiPageResponse<>();
        response.setPage(logPage.getCurrent());
        response.setSize(logPage.getSize());
        response.setTotal(logPage.getTotal());
        response.setTotalPages(logPage.getPages());
        response.setItems(items);
        return response;
    }

    @PostMapping("/run-now")
    public String runNow() {
        greetingMailService.runDailyJob();
        return "ok";
    }

    private MailLogApiResponse toResponse(MailSendLog entity) {
        MailLogApiResponse response = new MailLogApiResponse();
        response.setId(entity.getId());
        response.setCustomerId(entity.getCustomerId());
        response.setSendType(entity.getSendType());
        response.setHolidayCode(entity.getHolidayCode());
        response.setTargetDate(entity.getTargetDate());
        response.setToEmail(entity.getToEmail());
        response.setSubject(entity.getSubject());
        response.setStatus(entity.getStatus());
        response.setErrorMsg(entity.getErrorMsg());
        response.setRetryCount(entity.getRetryCount());
        response.setSentAt(entity.getSentAt());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}

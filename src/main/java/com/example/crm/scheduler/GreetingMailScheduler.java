package com.example.crm.scheduler;

import com.example.crm.service.GreetingMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GreetingMailScheduler {

    private final GreetingMailService greetingMailService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Singapore")
    public void runDailyGreetingTask() {
        greetingMailService.runDailyJob();
    }
}

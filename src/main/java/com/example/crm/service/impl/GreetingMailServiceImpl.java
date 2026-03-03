package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.Customer;
import com.example.crm.entity.MailSendLog;
import com.example.crm.entity.SgHolidayCalendar;
import com.example.crm.mapper.MailSendLogMapper;
import com.example.crm.service.CustomerService;
import com.example.crm.service.GreetingMailService;
import com.example.crm.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GreetingMailServiceImpl implements GreetingMailService {

    private final CustomerService customerService;
    private final HolidayService holidayService;
    private final MailSendLogMapper mailSendLogMapper;
    private final JavaMailSender mailSender;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void runDailyJob() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Singapore"));
        List<Customer> birthdayCustomers = customerService.findBirthdayCustomers(today);
        for (Customer customer : birthdayCustomers) {
            sendWithLog(customer, "birthday", "BIRTHDAY", today, "Happy Birthday, " + customer.getName());
        }

        List<SgHolidayCalendar> holidays = holidayService.findByDate(today);
        if (holidays.isEmpty()) {
            return;
        }
        List<Customer> allCustomers = customerService.list(null).stream()
                .filter(c -> StringUtils.hasText(c.getEmail()))
                .toList();
        for (SgHolidayCalendar holiday : holidays) {
            for (Customer customer : allCustomers) {
                sendWithLog(customer, "festival", holiday.getHolidayCode(), today,
                        "Greetings for " + holiday.getHolidayName() + ", " + customer.getName());
            }
        }
    }

    private void sendWithLog(Customer customer, String sendType, String holidayCode, LocalDate targetDate, String subject) {
        Long count = mailSendLogMapper.selectCount(new LambdaQueryWrapper<MailSendLog>()
                .eq(MailSendLog::getCustomerId, customer.getId())
                .eq(MailSendLog::getSendType, sendType)
                .eq(MailSendLog::getHolidayCode, holidayCode)
                .eq(MailSendLog::getTargetDate, targetDate));
        if (count != null && count > 0) {
            return;
        }

        MailSendLog logEntity = new MailSendLog();
        logEntity.setCustomerId(customer.getId());
        logEntity.setSendType(sendType);
        logEntity.setHolidayCode(holidayCode);
        logEntity.setTargetDate(targetDate);
        logEntity.setToEmail(customer.getEmail());
        logEntity.setSubject(subject);
        logEntity.setRetryCount(0);
        logEntity.setCreatedAt(LocalDateTime.now());

        int retries = 0;
        while (retries < 3) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(customer.getEmail());
                message.setSubject(subject);
                message.setText("Dear " + customer.getName() + ",\n\nWishing you joy and success.\n\nBest regards,\nCustomer Care Team");
                mailSender.send(message);
                logEntity.setStatus("success");
                logEntity.setSentAt(LocalDateTime.now());
                logEntity.setRetryCount(retries);
                mailSendLogMapper.insert(logEntity);
                return;
            } catch (Exception ex) {
                retries++;
                log.warn("mail send failed for customerId={} retry={}", customer.getId(), retries, ex);
                if (retries >= 3) {
                    logEntity.setStatus("failed");
                    logEntity.setErrorMsg(ex.getMessage());
                    logEntity.setRetryCount(retries);
                    mailSendLogMapper.insert(logEntity);
                }
            }
        }
    }
}

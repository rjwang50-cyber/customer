package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.SgHolidayCalendar;
import com.example.crm.mapper.SgHolidayCalendarMapper;
import com.example.crm.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final SgHolidayCalendarMapper holidayMapper;

    @Override
    public List<SgHolidayCalendar> findByDate(LocalDate dateInSingapore) {
        return holidayMapper.selectList(new LambdaQueryWrapper<SgHolidayCalendar>()
                .eq(SgHolidayCalendar::getHolidayDate, dateInSingapore));
    }
}

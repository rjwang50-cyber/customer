package com.example.crm.service;

import com.example.crm.entity.SgHolidayCalendar;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    List<SgHolidayCalendar> findByDate(LocalDate dateInSingapore);
}

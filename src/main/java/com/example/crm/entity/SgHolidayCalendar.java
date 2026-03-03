package com.example.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("sg_holiday_calendar")
public class SgHolidayCalendar {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate holidayDate;
    private String holidayCode;
    private String holidayName;
    private Integer year;
    private Integer isObserved;
}

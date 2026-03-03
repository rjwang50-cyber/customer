package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.entity.EmployeeUser;
import com.example.crm.mapper.EmployeeUserMapper;
import com.example.crm.service.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextServiceImpl implements UserContextService {

    private final EmployeeUserMapper employeeUserMapper;

    @Override
    public Long currentEmployeeId() {
        EmployeeUser user = employeeUserMapper.selectOne(new LambdaQueryWrapper<EmployeeUser>()
                .eq(EmployeeUser::getUsername, currentUsername())
                .last("limit 1"));
        return user == null ? null : user.getId();
    }

    @Override
    public String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }
}

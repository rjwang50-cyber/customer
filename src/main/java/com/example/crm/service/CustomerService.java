package com.example.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.crm.dto.CustomerForm;
import com.example.crm.entity.Customer;
import com.example.crm.entity.CustomerSocialAccount;

import java.time.LocalDate;
import java.util.List;

public interface CustomerService {
    IPage<Customer> page(String keyword, long pageNo, long pageSize);
    List<Customer> list(String keyword);
    Customer findById(Long id);
    List<CustomerSocialAccount> listSocialAccounts(Long customerId);
    Long save(CustomerForm form, Long employeeId);
    void delete(Long id);
    List<Customer> findBirthdayCustomers(LocalDate dateInSingapore);
}

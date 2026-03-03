package com.example.crm.service;

import com.example.crm.dto.CustomerForm;
import com.example.crm.entity.Customer;

import java.time.LocalDate;
import java.util.List;

public interface CustomerService {
    List<Customer> list(String keyword);
    Customer findById(Long id);
    void save(CustomerForm form, Long employeeId);
    void delete(Long id);
    List<Customer> findBirthdayCustomers(LocalDate dateInSingapore);
}

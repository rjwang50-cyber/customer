package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crm.dto.CustomerForm;
import com.example.crm.entity.Customer;
import com.example.crm.entity.CustomerSocialAccount;
import com.example.crm.mapper.CustomerMapper;
import com.example.crm.mapper.CustomerSocialAccountMapper;
import com.example.crm.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerSocialAccountMapper socialAccountMapper;

    @Override
    public List<Customer> list(String keyword) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .orderByDesc(Customer::getUpdatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                    .or().like(Customer::getPhone, keyword)
                    .or().like(Customer::getEmail, keyword));
        }
        return customerMapper.selectList(wrapper);
    }

    @Override
    public Customer findById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CustomerForm form, Long employeeId) {
        LocalDateTime now = LocalDateTime.now();
        Customer customer;
        if (form.getId() == null) {
            customer = new Customer();
            customer.setCreatedBy(employeeId);
            customer.setCreatedAt(now);
        } else {
            customer = customerMapper.selectById(form.getId());
            if (customer == null) {
                throw new IllegalArgumentException("Customer does not exist");
            }
        }
        customer.setName(form.getName());
        customer.setPhone(form.getPhone());
        customer.setEmail(form.getEmail());
        customer.setBirthday(form.getBirthday());
        customer.setHobbies(form.getHobbies());
        customer.setNotes(form.getNotes());
        customer.setUpdatedAt(now);

        if (form.getId() == null) {
            customerMapper.insert(customer);
        } else {
            customerMapper.updateById(customer);
            socialAccountMapper.delete(new LambdaQueryWrapper<CustomerSocialAccount>()
                    .eq(CustomerSocialAccount::getCustomerId, customer.getId()));
        }

        if (StringUtils.hasText(form.getSocialPlatform()) && StringUtils.hasText(form.getSocialAccount())) {
            CustomerSocialAccount account = new CustomerSocialAccount();
            account.setCustomerId(customer.getId());
            account.setPlatform(form.getSocialPlatform().trim());
            account.setAccount(form.getSocialAccount().trim());
            account.setCreatedAt(now);
            socialAccountMapper.insert(account);
        }
    }

    @Override
    public void delete(Long id) {
        socialAccountMapper.delete(new LambdaQueryWrapper<CustomerSocialAccount>()
                .eq(CustomerSocialAccount::getCustomerId, id));
        customerMapper.deleteById(id);
    }

    @Override
    public List<Customer> findBirthdayCustomers(LocalDate dateInSingapore) {
        List<Customer> all = customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                .isNotNull(Customer::getBirthday)
                .isNotNull(Customer::getEmail));
        return all.stream()
                .filter(c -> c.getBirthday().getMonthValue() == dateInSingapore.getMonthValue())
                .filter(c -> c.getBirthday().getDayOfMonth() == dateInSingapore.getDayOfMonth())
                .toList();
    }
}

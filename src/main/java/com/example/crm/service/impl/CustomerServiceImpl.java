package com.example.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerSocialAccountMapper socialAccountMapper;

    @Override
    public IPage<Customer> page(String keyword, long pageNo, long pageSize) {
        return customerMapper.selectPage(new Page<>(pageNo, pageSize), buildSearchWrapper(keyword));
    }

    @Override
    public List<Customer> list(String keyword) {
        return customerMapper.selectList(buildSearchWrapper(keyword));
    }

    @Override
    public Customer findById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public List<CustomerSocialAccount> listSocialAccounts(Long customerId) {
        return socialAccountMapper.selectList(new LambdaQueryWrapper<CustomerSocialAccount>()
                .eq(CustomerSocialAccount::getCustomerId, customerId)
                .orderByAsc(CustomerSocialAccount::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(CustomerForm form, Long employeeId) {
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
        customer.setPreferredLanguage(normalizeLanguage(form.getPreferredLanguage()));
        customer.setHobbies(form.getHobbies());
        customer.setNotes(form.getNotes());
        customer.setUpdatedAt(now);

        if (form.getId() == null) {
            customerMapper.insert(customer);
        } else {
            customerMapper.updateById(customer);
        }

        replaceSocialAccounts(customer.getId(), form.getSocialPlatforms(), form.getSocialAccounts(), now);
        return customer.getId();
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

    private void replaceSocialAccounts(Long customerId, List<String> platforms, List<String> accounts, LocalDateTime now) {
        socialAccountMapper.delete(new LambdaQueryWrapper<CustomerSocialAccount>()
                .eq(CustomerSocialAccount::getCustomerId, customerId));

        List<String> safePlatforms = platforms == null ? new ArrayList<>() : platforms;
        List<String> safeAccounts = accounts == null ? new ArrayList<>() : accounts;
        int max = Math.max(safePlatforms.size(), safeAccounts.size());
        for (int i = 0; i < max; i++) {
            String platform = i < safePlatforms.size() ? safePlatforms.get(i) : null;
            String account = i < safeAccounts.size() ? safeAccounts.get(i) : null;
            if (!StringUtils.hasText(platform) || !StringUtils.hasText(account)) {
                continue;
            }
            CustomerSocialAccount entity = new CustomerSocialAccount();
            entity.setCustomerId(customerId);
            entity.setPlatform(platform.trim());
            entity.setAccount(account.trim());
            entity.setCreatedAt(now);
            socialAccountMapper.insert(entity);
        }
    }

    private String normalizeLanguage(String preferredLanguage) {
        if (!StringUtils.hasText(preferredLanguage)) {
            return "en";
        }
        String value = preferredLanguage.trim().toLowerCase();
        return switch (value) {
            case "zh", "zh-cn", "zh-hans" -> "zh";
            default -> "en";
        };
    }

    private LambdaQueryWrapper<Customer> buildSearchWrapper(String keyword) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .orderByDesc(Customer::getUpdatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                    .or().like(Customer::getPhone, keyword)
                    .or().like(Customer::getEmail, keyword));
        }
        return wrapper;
    }
}

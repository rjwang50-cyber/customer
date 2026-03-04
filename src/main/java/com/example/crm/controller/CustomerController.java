package com.example.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.crm.dto.CustomerForm;
import com.example.crm.entity.Customer;
import com.example.crm.entity.CustomerSocialAccount;
import com.example.crm.service.CustomerService;
import com.example.crm.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserContextService userContextService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") long page,
                       @RequestParam(defaultValue = "12") long size,
                       Model model) {
        IPage<Customer> customerPage = customerService.page(keyword, page, size);
        model.addAttribute("customers", customerPage.getRecords());
        model.addAttribute("currentPage", customerPage.getCurrent());
        model.addAttribute("totalPages", customerPage.getPages());
        model.addAttribute("hasPrevious", customerPage.getCurrent() > 1);
        model.addAttribute("hasNext", customerPage.getCurrent() < customerPage.getPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);
        return "customer/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        CustomerForm form = new CustomerForm();
        form.getSocialPlatforms().add("");
        form.getSocialAccounts().add("");
        model.addAttribute("customerForm", form);
        return "customer/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.findById(id);
        CustomerForm form = new CustomerForm();
        form.setId(customer.getId());
        form.setName(customer.getName());
        form.setPhone(customer.getPhone());
        form.setEmail(customer.getEmail());
        form.setBirthday(customer.getBirthday());
        form.setPreferredLanguage(StringUtils.hasText(customer.getPreferredLanguage()) ? customer.getPreferredLanguage() : "en");
        form.setHobbies(customer.getHobbies());
        form.setNotes(customer.getNotes());
        List<CustomerSocialAccount> accounts = customerService.listSocialAccounts(id);
        if (accounts.isEmpty()) {
            form.getSocialPlatforms().add("");
            form.getSocialAccounts().add("");
        } else {
            for (CustomerSocialAccount account : accounts) {
                form.getSocialPlatforms().add(account.getPlatform());
                form.getSocialAccounts().add(account.getAccount());
            }
        }
        model.addAttribute("customerForm", form);
        return "customer/form";
    }

    @PostMapping
    public String save(@Valid CustomerForm customerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            if (customerForm.getSocialPlatforms().isEmpty()) {
                customerForm.getSocialPlatforms().add("");
                customerForm.getSocialAccounts().add("");
            }
            return "customer/form";
        }
        customerService.save(customerForm, userContextService.currentEmployeeId());
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        customerService.delete(id);
        return "redirect:/customers";
    }
}

package com.example.crm.controller;

import com.example.crm.dto.CustomerForm;
import com.example.crm.entity.Customer;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserContextService userContextService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("customers", customerService.list(keyword));
        model.addAttribute("keyword", keyword);
        return "customer/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customerForm", new CustomerForm());
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
        form.setHobbies(customer.getHobbies());
        form.setNotes(customer.getNotes());
        model.addAttribute("customerForm", form);
        return "customer/form";
    }

    @PostMapping
    public String save(@Valid CustomerForm customerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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

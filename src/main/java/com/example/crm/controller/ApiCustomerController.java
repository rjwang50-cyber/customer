package com.example.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.crm.dto.CustomerForm;
import com.example.crm.dto.api.ApiPageResponse;
import com.example.crm.dto.api.CustomerApiResponse;
import com.example.crm.dto.api.CustomerUpsertRequest;
import com.example.crm.dto.api.SocialAccountDto;
import com.example.crm.entity.Customer;
import com.example.crm.service.CustomerService;
import com.example.crm.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class ApiCustomerController {

    private final CustomerService customerService;
    private final UserContextService userContextService;

    @GetMapping
    public ApiPageResponse<CustomerApiResponse> list(@RequestParam(required = false) String keyword,
                                                     @RequestParam(defaultValue = "1") long page,
                                                     @RequestParam(defaultValue = "20") long size) {
        IPage<Customer> customerPage = customerService.page(keyword, page, size);
        List<CustomerApiResponse> items = customerPage.getRecords().stream()
                .map(customer -> toResponse(customer, false))
                .toList();
        ApiPageResponse<CustomerApiResponse> response = new ApiPageResponse<>();
        response.setPage(customerPage.getCurrent());
        response.setSize(customerPage.getSize());
        response.setTotal(customerPage.getTotal());
        response.setTotalPages(customerPage.getPages());
        response.setItems(items);
        return response;
    }

    @GetMapping("/{id}")
    public CustomerApiResponse detail(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return toResponse(customer, true);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerApiResponse create(@Valid @RequestBody CustomerUpsertRequest request) {
        Long id = customerService.save(toForm(request, null), userContextService.currentEmployeeId());
        Customer customer = customerService.findById(id);
        return toResponse(customer, true);
    }

    @PutMapping("/{id}")
    public CustomerApiResponse update(@PathVariable Long id, @Valid @RequestBody CustomerUpsertRequest request) {
        Customer existing = customerService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customerService.save(toForm(request, id), userContextService.currentEmployeeId());
        return toResponse(customerService.findById(id), true);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Customer existing = customerService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customerService.delete(id);
    }

    private CustomerForm toForm(CustomerUpsertRequest request, Long id) {
        CustomerForm form = new CustomerForm();
        form.setId(id);
        form.setName(request.getName());
        form.setPhone(request.getPhone());
        form.setEmail(request.getEmail());
        form.setBirthday(request.getBirthday());
        form.setPreferredLanguage(request.getPreferredLanguage());
        form.setHobbies(request.getHobbies());
        form.setNotes(request.getNotes());
        List<SocialAccountDto> socialAccounts = request.getSocialAccounts() == null ? new ArrayList<>() : request.getSocialAccounts();
        form.setSocialPlatforms(socialAccounts.stream().map(SocialAccountDto::getPlatform).toList());
        form.setSocialAccounts(socialAccounts.stream().map(SocialAccountDto::getAccount).toList());
        return form;
    }

    private CustomerApiResponse toResponse(Customer customer, boolean includeSocial) {
        CustomerApiResponse response = new CustomerApiResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setBirthday(customer.getBirthday());
        response.setPreferredLanguage(customer.getPreferredLanguage());
        response.setHobbies(customer.getHobbies());
        response.setNotes(customer.getNotes());
        response.setCreatedBy(customer.getCreatedBy());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        if (includeSocial) {
            List<SocialAccountDto> socials = customerService.listSocialAccounts(customer.getId()).stream().map(account -> {
                SocialAccountDto dto = new SocialAccountDto();
                dto.setPlatform(account.getPlatform());
                dto.setAccount(account.getAccount());
                return dto;
            }).toList();
            response.setSocialAccounts(socials);
        }
        return response;
    }
}

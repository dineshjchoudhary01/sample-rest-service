package com.example.rest.service;

import com.example.rest.dto.CustomerCreateDto;
import com.example.rest.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerService {
    Customer create(CustomerCreateDto dto);
    Optional<Customer> getById(Long id);
    Page<Customer> list(Pageable pageable);
    Customer update(Long id, CustomerCreateDto dto);
    void softDelete(Long id);
}

package com.example.rest.service.impl;

import com.example.rest.dto.CustomerCreateDto;
import com.example.rest.model.Address;
import com.example.rest.model.Contact;
import com.example.rest.model.Customer;
import com.example.rest.repository.CustomerRepository;
import com.example.rest.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Customer create(CustomerCreateDto dto) {
        Customer c = new Customer();
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setSsn(dto.getSsn());
        if (dto.getAddresses() != null) {
            dto.getAddresses().forEach(aDto -> {
                Address a = new Address();
                a.setStreet(aDto.getStreet());
                a.setCity(aDto.getCity());
                a.setState(aDto.getState());
                a.setPostalCode(aDto.getPostalCode());
                c.addAddress(a);
            });
        }
        if (dto.getContacts() != null) {
            dto.getContacts().forEach(ctDto -> {
                Contact ct = new Contact();
                ct.setType(ctDto.getType());
                ct.setValue(ctDto.getValue());
                c.addContact(ct);
            });
        }
        return repo.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getById(Long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Customer update(Long id, CustomerCreateDto dto) {
        Customer existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setSsn(dto.getSsn());

        // replace addresses and contacts (simple strategy)
        existing.getAddresses().clear();
        if (dto.getAddresses() != null) {
            dto.getAddresses().forEach(aDto -> {
                Address a = new Address();
                a.setStreet(aDto.getStreet());
                a.setCity(aDto.getCity());
                a.setState(aDto.getState());
                a.setPostalCode(aDto.getPostalCode());
                existing.addAddress(a);
            });
        }

        existing.getContacts().clear();
        if (dto.getContacts() != null) {
            dto.getContacts().forEach(ctDto -> {
                Contact ct = new Contact();
                ct.setType(ctDto.getType());
                ct.setValue(ctDto.getValue());
                existing.addContact(ct);
            });
        }

        return repo.save(existing);
    }

    @Override
    public void softDelete(Long id) {
        // leveraging @SQLDelete; calling delete will run SQLDelete
        repo.deleteById(id);
    }
}

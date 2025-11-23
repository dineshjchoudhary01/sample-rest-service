package com.example.rest.mapper;

import com.example.rest.dto.*;
import com.example.rest.model.Address;
import com.example.rest.model.Contact;
import com.example.rest.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateDto dto) {
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
        return c;
    }

    public CustomerResponseDto toResponseDto(Customer c) {
        CustomerResponseDto r = new CustomerResponseDto();
        r.setId(c.getId());
        r.setFirstName(c.getFirstName());
        r.setLastName(c.getLastName());
        r.setSsnMasked(maskSsn(c.getSsn()));
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        r.setAddresses(c.getAddresses().stream().map(this::toAddressDto).collect(Collectors.toList()));
        r.setContacts(c.getContacts().stream().map(this::toContactDto).collect(Collectors.toList()));
        return r;
    }

    private AddressDto toAddressDto(Address a) {
        AddressDto d = new AddressDto();
        d.setId(a.getId());
        d.setStreet(a.getStreet());
        d.setCity(a.getCity());
        d.setState(a.getState());
        d.setPostalCode(a.getPostalCode());
        return d;
    }

    private ContactDto toContactDto(Contact c) {
        ContactDto d = new ContactDto();
        d.setId(c.getId());
        d.setType(c.getType());
        d.setValue(c.getValue());
        return d;
    }

    private String maskSsn(String ssn) {
        if (ssn == null) return null;
        String digits = ssn.replaceAll("\\D", "");
        int len = digits.length();
        if (len <= 4) return "****" + digits;
        String last4 = digits.substring(len - 4);
        return "****-****-" + last4;
    }
}

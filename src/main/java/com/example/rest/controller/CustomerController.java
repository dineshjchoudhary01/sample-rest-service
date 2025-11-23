package com.example.rest.controller;

import com.example.rest.dto.CustomerCreateDto;
import com.example.rest.dto.CustomerResponseDto;
import com.example.rest.dto.api.JsonApiData;
import com.example.rest.dto.api.JsonApiRequest;
import com.example.rest.dto.api.JsonApiResponse;
import com.example.rest.mapper.CustomerMapper;
import com.example.rest.model.Customer;
import com.example.rest.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Create (JSON:API style)
     */
    @PostMapping
    public ResponseEntity<JsonApiResponse<CustomerResponseDto>> create(
            @Valid @RequestBody JsonApiRequest<CustomerCreateDto> request
    ) {
        CustomerCreateDto dto = request.data.attributes;
        Customer created = service.create(dto);
        CustomerResponseDto respDto = mapper.toResponseDto(created);
        JsonApiData<CustomerResponseDto> data = new JsonApiData<>();
        data.id = String.valueOf(respDto.getId());
        data.type = "customers";
        data.attributes = respDto;
        JsonApiResponse<CustomerResponseDto> response = new JsonApiResponse<>();
        response.data = data;
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Get by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<JsonApiResponse<CustomerResponseDto>> get(@PathVariable Long id) {
        Customer c = service.getById(id).orElseThrow(() -> new NoSuchElementException("Customer not found"));
        CustomerResponseDto dto = mapper.toResponseDto(c);
        JsonApiData<CustomerResponseDto> data = new JsonApiData<>();
        data.id = String.valueOf(dto.getId());
        data.type = "customers";
        data.attributes = dto;
        JsonApiResponse<CustomerResponseDto> response = new JsonApiResponse<>();
        response.data = data;
        return ResponseEntity.ok(response);
    }

    /**
     * List + pagination
     * Query params: page (0-based), size, sort (e.g. createdAt,desc)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction dir = Sort.Direction.fromString(sortParts.length > 1 ? sortParts[1] : "asc");
        String prop = sortParts.length > 0 ? sortParts[0] : "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, prop));
        Page<Customer> p = service.list(pageable);

        List<JsonApiData<CustomerResponseDto>> data = p.getContent().stream()
                .map(mapper::toResponseDto)
                .map(dto -> {
                    JsonApiData<CustomerResponseDto> d = new JsonApiData<>();
                    d.id = String.valueOf(dto.getId());
                    d.type = "customers";
                    d.attributes = dto;
                    return d;
                })
                .collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("data", data);
        Map<String, Object> meta = new HashMap<>();
        meta.put("page", p.getNumber());
        meta.put("size", p.getSize());
        meta.put("totalElements", p.getTotalElements());
        meta.put("totalPages", p.getTotalPages());
        body.put("meta", meta);

        return ResponseEntity.ok(body);
    }

    /**
     * Update (JSON:API)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<JsonApiResponse<CustomerResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody JsonApiRequest<CustomerCreateDto> request
    ) {
        CustomerCreateDto dto = request.data.attributes;
        Customer updated = service.update(id, dto);
        CustomerResponseDto resp = mapper.toResponseDto(updated);
        JsonApiData<CustomerResponseDto> data = new JsonApiData<>();
        data.id = String.valueOf(resp.getId());
        data.type = "customers";
        data.attributes = resp;
        JsonApiResponse<CustomerResponseDto> response = new JsonApiResponse<>();
        response.data = data;
        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.rest.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonApiRequest<T> {
    @NotNull
    @Valid
    public JsonApiData<T> data;
}

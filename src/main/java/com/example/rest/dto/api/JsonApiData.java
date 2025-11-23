package com.example.rest.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonApiData<T> {
    public String id;
    public String type;

    @JsonProperty("attributes")
    @Valid
    public T attributes;


}

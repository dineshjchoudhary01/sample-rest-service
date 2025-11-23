package com.example.rest.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiResponse<T> {
    public JsonApiData<T> data;
    public Object meta;
}

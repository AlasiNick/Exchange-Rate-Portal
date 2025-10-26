package com.seb.api.service.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlBuilder {
    private final String baseUrl;
    private final Map<String, String> params = new LinkedHashMap<>();

    private UrlBuilder(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static UrlBuilder from(String baseUrl) {
        return new UrlBuilder(baseUrl);
    }

    public UrlBuilder param(String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value);
        }
        return this;
    }

    public String build() {
        if (params.isEmpty()) {
            return baseUrl;
        }

        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return baseUrl + "?" + queryString;
    }
}

package com.example.urlshortner.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ShortenUrlRequest {
    @NotBlank(message = "Original URL cannot be empty")
    @Pattern(
            regexp = "^https?://.+",
            message = "url should start with http:// or https://")
    private String originalUrl;
    private String customAlias;
    @Positive(message = "Expiry hours must be greater than Zero")
    private Long expiresInHours;

    public Long getExpiresInHours() {
        return expiresInHours;
    }

    public void setExpiresInHours(Long expiresInHours) {
        this.expiresInHours = expiresInHours;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}

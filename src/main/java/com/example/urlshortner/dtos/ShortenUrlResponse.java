package com.example.urlshortner.dtos;

import java.time.LocalDateTime;

public class ShortenUrlResponse {
    private String ShortCode;
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime expiresAt;

    public ShortenUrlResponse(String originalUrl, String shortUrl, String shortCode, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.ShortCode = shortCode;
        this.expiresAt = this.expiresAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getShortCode() {
        return ShortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}

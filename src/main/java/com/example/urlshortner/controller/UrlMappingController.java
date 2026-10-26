package com.example.urlshortner.controller;

import com.example.urlshortner.dtos.ShortenUrlRequest;
import com.example.urlshortner.dtos.ShortenUrlResponse;
import com.example.urlshortner.dtos.UrlStatsresponse;
import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.service.UrlMappingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/apis/url")
public class UrlMappingController {
    @Autowired
    private UrlMappingService urlMappingService;
    @Value("${app.base-url}")
    private String baseUrl;

    @PostMapping(value = "/shorten", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ShortenUrlResponse createShortUrl(@Valid @RequestBody ShortenUrlRequest request) {
        UrlMapping mapping =  urlMappingService.createShortUrl(request.getOriginalUrl(), request.getCustomAlias(), request.getExpiresInHours());
        String shortUrl = baseUrl+ "/apis/url/"+ mapping.getShortCode();
        return new ShortenUrlResponse(mapping.getoriginalUrl(), shortUrl, mapping.getShortCode(), mapping.getExpiresAt());
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable("shortCode") String shortCode) {
        UrlMapping mapping = urlMappingService.getByShortCode(shortCode);
        if(urlMappingService.getExpires(mapping)) {
            throw new ResponseStatusException(HttpStatus.GONE, "This url has been expired");
        }
        urlMappingService.incrementClickCount(mapping);
        return new RedirectView(mapping.getoriginalUrl());
    }

    @GetMapping("stats/{shortCode}")
    public UrlStatsresponse getUrlStats(@PathVariable String shortCode) {
        UrlMapping mapping = urlMappingService.getByShortCode(shortCode);
        String shorturl = baseUrl + "/apis/url/" + mapping.getShortCode();
        return new UrlStatsresponse(mapping.getoriginalUrl(), mapping.getShortCode(), shorturl, mapping.getCreatedAt(), mapping.getExpiresAt(), mapping.getClickCount());
    }


}


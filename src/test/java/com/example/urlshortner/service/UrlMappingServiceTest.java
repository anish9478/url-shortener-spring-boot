package com.example.urlshortner.service;

import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlMappingServiceTest {
    @Mock
    private UrlMappingRepository urlMappingRepository;
    @InjectMocks
    private UrlMappingService urlMappingService;
    @Test
    void shouldCreateUrlWithOriginalUrl() {
        String originalUrl = "https://example.com";
        String alias = "example-24";
        when(urlMappingRepository.findFirstByOriginalUrlOrderByIdAsc(originalUrl)).thenReturn(Optional.<UrlMapping>empty());
        when(urlMappingRepository.existsByShortCode(alias)).thenReturn(false);
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime before = LocalDateTime.now();
        UrlMapping result = urlMappingService.createShortUrl(originalUrl, alias, 24L);
        LocalDateTime after = LocalDateTime.now();
        assertEquals(originalUrl, result.getoriginalUrl());
        assertEquals(alias, result.getShortCode());
        assertNotNull(result.getExpiresAt());
        assertFalse(result.getExpiresAt().isBefore(before.plusHours(24)));
        assertFalse(result.getExpiresAt().isAfter(after.plusHours(24)));
        verify(urlMappingRepository).save(any(UrlMapping.class));
    }
    @Test
    void shouldThrowException() {
        String originalUrl = "https://example.com";
        String alias = "example-24";
        when(urlMappingRepository.findFirstByOriginalUrlOrderByIdAsc(originalUrl)).thenReturn(Optional.<UrlMapping>empty());
        when(urlMappingRepository.existsByShortCode(alias)).thenReturn(true);
        org.springframework.web.server.ResponseStatusException exception = assertThrows
                (org.springframework.web.server.ResponseStatusException.class,
                ()-> urlMappingService.createShortUrl(originalUrl, alias, 24L));
        assertEquals(409, exception.getStatusCode().value());
        verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    }
    @Test
    void hasError() {
        String originalUrl = "https://example.com";
        String alias = "ex";
        when(urlMappingRepository.findFirstByOriginalUrlOrderByIdAsc(originalUrl)).thenReturn(Optional.<UrlMapping>empty());
        org.springframework.web.server.ResponseStatusException exception =
                assertThrows(org.springframework.web.server.ResponseStatusException.class,
                ()->urlMappingService.createShortUrl(originalUrl, alias, 24L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(urlMappingRepository, never()).save(any(UrlMapping.class));

    }
    @Test
    void shouldReturnUrlOnShortCode() {
        String alias = "ex12345";
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(alias);
        mapping.setoriginalUrl("https://example.com");
        when(urlMappingRepository.findByShortCode(alias)).thenReturn(Optional.of(mapping));
        UrlMapping result = urlMappingService.getByShortCode(alias);
        assertNotNull(result);
        assertEquals(alias, result.getShortCode());
        assertEquals("https://example.com", result.getoriginalUrl());

    }
    @Test
    void shortCodeNotFound() {
        String alias = "expected99";
        when(urlMappingRepository.findByShortCode(alias)).thenReturn(Optional.empty());
        assertThrows(com.example.urlshortner.exception.ShortCodeNotFoundException.class,
                () ->urlMappingService.getByShortCode(alias));
    }
    @Test
    void increaseClickCount() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("abc12345");
        mapping.setClickCount(0L);
        when(urlMappingRepository.save(any(UrlMapping.class))).
                thenAnswer(invocation -> invocation.getArgument(0));
        urlMappingService.incrementClickCount(mapping);
        assertEquals(1L, mapping.getClickCount());
        verify(urlMappingRepository).save(mapping);
    }
    @Test
    void trueWhenUrlExpired() {
        UrlMapping mapping = new UrlMapping();
        mapping.setExpiresAt(LocalDateTime.now().minusHours(1));
        assertTrue(urlMappingService.getExpires(mapping));
    }

    @Test
    void falseWhenUrlExpired() {
        UrlMapping mapping = new UrlMapping();
        mapping.setExpiresAt(LocalDateTime.now().plusHours(1));
        assertFalse(urlMappingService.getExpires(mapping));
    }
}

package com.example.urlshortner.controller;

import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.exception.ShortCodeNotFoundException;
import com.example.urlshortner.service.UrlMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(
        controllers = UrlMappingController.class,
        properties = "app.base-url=https://localhost:8080"
)
public class UrlMappingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UrlMappingService urlMappingService;
    @Test
    void return400WhenInvalid() throws Exception{
        mockMvc.perform(post("/apis/url/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"originalUrl":"invalid-url"}
                       """)).andExpect(status().isBadRequest());
    }
    @Test
    void shouldCreateShortUrl() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setoriginalUrl("https://example.com");
        mapping.setShortCode("abc12345");
        mapping.setExpiresAt(null);
        when(urlMappingService.
                createShortUrl("https://example.com", null, null))
                .thenReturn(mapping);
        mockMvc.perform(post("/apis/url/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"originalUrl" : "https://example.com"}
                        """)).andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://example.com"))
                .andExpect(jsonPath("$.ShortCode").value("abc12345"))
                .andExpect(jsonPath("$.shortUrl")
                .value("https://localhost:8080/apis/url/abc12345"));
    }
    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setoriginalUrl("https://example.com");
        mapping.setShortCode("abc12345");
        when(urlMappingService.getByShortCode("abc12345")).thenReturn(mapping);
        when(urlMappingService.getExpires(mapping)).thenReturn(false);
        mockMvc.perform(get("/apis/url/abc12345"))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "https://example.com"));
        verify(urlMappingService).incrementClickCount(mapping);
    }
    @Test
    void getStatsUrl() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setoriginalUrl("https://example.com");
        mapping.setShortCode("abc12345");
        when(urlMappingService.getByShortCode("abc12345")).thenReturn(mapping);
        mockMvc.perform(get("/apis/url/stats/abc12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://example.com"));
    }
    @Test
    void shouldReturn404WhenShortCodeNotFound() throws Exception {
        when(urlMappingService.getByShortCode("unknown123"))
                .thenThrow(new ShortCodeNotFoundException("Url not found"));
        mockMvc.perform(get("/apis/url/unknown123")).andExpect(status().isNotFound());
    }
    @Test
    void shouldReturn410WhenUrlExpired() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setoriginalUrl("https://example.com");
        mapping.setShortCode("abc12345");
        when(urlMappingService.getByShortCode("abc12345")).thenReturn(mapping);
        when(urlMappingService.getExpires(mapping)).thenReturn(true);
        mockMvc.perform(get("/apis/url/abc12345")).andExpect(status().is(410));
        verify(urlMappingService, never()).incrementClickCount(mapping);

    }
}

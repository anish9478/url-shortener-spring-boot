
package com.example.urlshortner.service;

import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.exception.ShortCodeNotFoundException;
import com.example.urlshortner.repository.UrlMappingRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UrlMappingService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    public UrlMapping createShortUrl(String originalUrl, String customAlias, Long expiresInHours) {
        Optional<UrlMapping> existingMapping = urlMappingRepository.findFirstByOriginalUrlOrderByIdAsc(originalUrl);
        if(existingMapping.isPresent()) {
            return existingMapping.get();
        }
        String shortCode = "";
        if(customAlias != null && !customAlias.isBlank()) {
            if(!customAlias.matches("[a-zA-Z0-9-]{3,20}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"custom alias must be between 3-20 characters : letters, numbers");
            }
            if (urlMappingRepository.existsByShortCode(customAlias)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"This custom alias is already taken");
            }
            shortCode = customAlias;
        }
        else {
            shortCode = generateUniqueShortCode();
        }

        UrlMapping mapping = new UrlMapping();
        if(expiresInHours != null) {
            mapping.setExpiresAt(LocalDateTime.now().plusHours(expiresInHours));
        }
        mapping.setoriginalUrl(originalUrl);
        mapping.setShortCode(shortCode);

        return urlMappingRepository.save(mapping);
    }

    public UrlMapping getByShortCode(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(()-> new ShortCodeNotFoundException("Short code could not be formed " + shortCode) );

    }

    public void incrementClickCount(UrlMapping urlMapping) {
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do{
            shortCode = UUID.randomUUID().toString().substring(0, 8);
        }
        while (urlMappingRepository.existsByShortCode(shortCode));
        return shortCode;
    }
    public boolean getExpires(UrlMapping mapping) {
        return mapping.getExpiresAt() != null && mapping.getExpiresAt().isBefore(LocalDateTime.now());
    }
}

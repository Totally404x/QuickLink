package com.ankit.quicklink.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class LinkUpdateRequestDTO {

    @URL
    private String originalUrl=null;
    private LocalDateTime expiresAt=null;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

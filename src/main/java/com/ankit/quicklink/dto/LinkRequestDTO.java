package com.ankit.quicklink.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class LinkRequestDTO {

    @URL @NotBlank
    private String originalUrl;
    @Nullable
    private LocalDateTime expiresAt=null;
    @Nullable
    private boolean oneTime=false;
    @Nullable @Positive
    private Integer maxClick=null;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean getOneTime() {
        return oneTime;
    }

    public Integer getMaxClick() {
        return maxClick;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public void setMaxClick(Integer maxClick) {
        this.maxClick = maxClick;
    }
}

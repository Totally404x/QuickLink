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
    private String customAlias=null;
    @Nullable
    private LocalDateTime scheduledAt=null;
    @Nullable
    private LocalDateTime expiresAt=null;
    @Nullable
    private String password=null;
    @Nullable
    private boolean oneTime=false;
    @Nullable @Positive
    private Integer maxClick=null;

    public String getOriginalUrl() {
        return originalUrl;
    }

    @Nullable
    public String getCustomAlias() {
        return customAlias;
    }

    @Nullable
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    @Nullable
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Nullable
    public Boolean getOneTime() {
        return oneTime;
    }

    @Nullable
    public Integer getMaxClick() {
        return maxClick;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setCustomAlias(@Nullable String customAlias) {
        this.customAlias = customAlias;
    }

    public void setScheduledAt(@Nullable LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void setExpiresAt(@Nullable LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setPassword(@Nullable String password) {
        this.password=password;
    }

    public void setOneTime(@Nullable Boolean oneTime) {
        this.oneTime = oneTime;
    }

    public void setMaxClick(@Nullable Integer maxClick) {
        this.maxClick = maxClick;
    }
}

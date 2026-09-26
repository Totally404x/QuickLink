package com.ankit.quicklink.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="links", indexes={@Index(name="idx_created_at", columnList="created_at")})
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private Long id;
    @Column(nullable=false)
    private String originalUrl;
    @Column(nullable=false, unique=true)
    private String shortCode;
    @Column(nullable=false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkStatus status=LinkStatus.ACTIVE;
    @Column(nullable=false)
    private Integer clicks=0;
    @Positive
    private Integer maxClick;
    @OneToMany(mappedBy = "link", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<ClickEvent> clickEvents;

    public Link() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public LinkStatus getStatus() {
        return status;
    }

    public void setStatus(LinkStatus status) {
        this.status = status;
    }

    public Integer getMaxClick() {
        return maxClick;
    }

    public void setMaxClick(Integer maxClick) {
        this.maxClick = maxClick;
    }

    public List<ClickEvent> getClickEvents() {
        return clickEvents;
    }

    public void setClickEvents(List<ClickEvent> clickEvents) {
        this.clickEvents = clickEvents;
    }
}

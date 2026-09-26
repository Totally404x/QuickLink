package com.ankit.quicklink.controller;

import com.ankit.quicklink.dto.LinkAnalyticsDTO;
import com.ankit.quicklink.dto.LinkRequestDTO;
import com.ankit.quicklink.dto.LinkUpdateRequestDTO;
import com.ankit.quicklink.entity.Link;
import com.ankit.quicklink.repository.DailyClickProjection;
import com.ankit.quicklink.service.LinkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ankit.quicklink.dto.LinkStatusRequestDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/links")
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService=linkService;
    }

    @PostMapping
    public Link createLink(@RequestBody LinkRequestDTO requestDTO) {
        return linkService.createLink(requestDTO.getOriginalUrl(), requestDTO.getExpiresAt(), requestDTO.getMaxClick());
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl= linkService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @GetMapping
    public Page<Link> getAllLinks(Pageable pageable) {
        return linkService.getAllLinks(pageable);
    }

    @GetMapping("/info/{id}")
    public Link getLinkById(@PathVariable Long id) {
        return linkService.getLinkById(id);
    }

    @GetMapping("/search")
    public Page<Link> getLinkByOriginalUrl(@RequestParam String originalUrl, Pageable pageable) {
        return linkService.getLinkByOriginalUrl(originalUrl, pageable);
    }

    @GetMapping("/{id}/analytics")
    public LinkAnalyticsDTO getLinkAnalytics(@PathVariable Long id) {
        return linkService.getlinkAnalytics(id);
    }

    @GetMapping("/{id}/analytics/daily")
    public List<DailyClickProjection> getDailyClicks(@PathVariable Long id) {
        return linkService.getDailyClicks(id);
    }

    @PutMapping("/URL/{id}")
    public Link updateLinkUrl(@PathVariable Long id, @RequestBody LinkUpdateRequestDTO linkUpdateRequestDTO) {
        return linkService.updateLinkUrl(id, linkUpdateRequestDTO.getOriginalUrl());
    }

    @PutMapping("/expiry/{id}")
    public Link updateLinkExpiry(@PathVariable Long id, @RequestBody LinkUpdateRequestDTO linkUpdateRequestDTO) {
        return linkService.updateLinkExpiry(id, linkUpdateRequestDTO.getExpiresAt());
    }

    @PutMapping("/status/{id}")
    public Link updateLinkStatus(@PathVariable Long id, @RequestBody LinkStatusRequestDTO linkStatusRequestDTO) {
        return linkService.updateLinkStatus(id, linkStatusRequestDTO.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        linkService.deleteLink(id);
        return ResponseEntity.noContent().build();
    }
}

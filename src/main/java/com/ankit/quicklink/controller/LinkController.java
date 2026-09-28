package com.ankit.quicklink.controller;

import com.ankit.quicklink.dto.*;
import com.ankit.quicklink.entity.Link;
import com.ankit.quicklink.mapper.LinkMapper;
import com.ankit.quicklink.mapper.ResponseMapper;
import com.ankit.quicklink.repository.DailyClickProjection;
import com.ankit.quicklink.service.LinkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/links")
public class LinkController {
    private final LinkService linkService;
    private final ResponseMapper responseMapper;

    public LinkController(LinkService linkService, ResponseMapper responseMapper) {
        this.linkService=linkService;
        this.responseMapper=responseMapper;
    }

    @PostMapping
    public ResponseEntity<String> createLink(@RequestBody LinkRequestDTO requestDTO) {
        Link link= linkService.createLink(requestDTO.getOriginalUrl(), requestDTO.getExpiresAt(), requestDTO.getMaxClick());
        LinkResponseDTO responseDTO=responseMapper.toResponse(link);
        return ResponseEntity.ok("Link created: \n"+responseDTO);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl= linkService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @GetMapping
    public ResponseEntity<Page<LinkResponseDTO>> getAllLinks(Pageable pageable) {
        Page<Link> links=linkService.getAllLinks(pageable);
        Page<LinkResponseDTO> responseDTOs=links.map(responseMapper::toResponse);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<LinkResponseDTO> getLinkById(@PathVariable Long id) {
        Link link= linkService.getLinkById(id);
        LinkResponseDTO responseDTO=responseMapper.toResponse(link);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LinkResponseDTO>> getLinkByOriginalUrl(@RequestParam String originalUrl, Pageable pageable) {
        Page<Link> links= linkService.getLinkByOriginalUrl(originalUrl, pageable);
        Page<LinkResponseDTO> responseDTOs=links.map(responseMapper::toResponse);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<LinkAnalyticsDTO> getLinkAnalytics(@PathVariable Long id) {
        LinkAnalyticsDTO analyticsDTO= linkService.getlinkAnalytics(id);
        return ResponseEntity.ok(analyticsDTO);
    }

    @GetMapping("/{id}/analytics/daily")
    public ResponseEntity<List<DailyClickProjection>> getDailyClicks(@PathVariable Long id) {
        List<DailyClickProjection> clickProjections= linkService.getDailyClicks(id);
        return ResponseEntity.ok(clickProjections);
    }

    @PutMapping("/URL/{id}")
    public ResponseEntity<String> updateLinkUrl(@PathVariable Long id, @RequestBody LinkUpdateRequestDTO linkUpdateRequestDTO) {
        boolean update= linkService.updateLinkUrl(id, linkUpdateRequestDTO.getOriginalUrl());
        if(update)
            return ResponseEntity.ok("Link (id:"+id+") Original URL has been changed to- "+linkUpdateRequestDTO.getOriginalUrl());
        return ResponseEntity.internalServerError().body("Link (id:" + id + ") URL change failed.");
    }

    @PutMapping("/expiry/{id}")
    public ResponseEntity<String> updateLinkExpiry(@PathVariable Long id, @RequestBody LinkUpdateRequestDTO linkUpdateRequestDTO) {
        boolean update= linkService.updateLinkExpiry(id, linkUpdateRequestDTO.getExpiresAt());
        if(update)
            return ResponseEntity.ok("Link (id:"+id+") expiry has been changed to- "+linkUpdateRequestDTO.getExpiresAt());
        return ResponseEntity.internalServerError().body("Link (id:" + id + ") expiry change failed.");
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<String> updateLinkStatus(@PathVariable Long id, @RequestBody LinkStatusRequestDTO linkStatusRequestDTO) {
        boolean update= linkService.updateLinkStatus(id, linkStatusRequestDTO.getStatus());
        if(update)
            return ResponseEntity.ok("Link (id:"+id+") status has been changed to- "+linkStatusRequestDTO.getStatus());
        return ResponseEntity.internalServerError().body("Link (id:" + id + ") status change failed.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLink(@PathVariable Long id) {
        boolean delete=linkService.deleteLink(id);
        if(delete)
            return ResponseEntity.ok("Link (id:"+id+")has been deleted.");
        return ResponseEntity.noContent().build();
    }
}

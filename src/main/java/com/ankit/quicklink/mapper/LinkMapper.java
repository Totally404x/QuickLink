package com.ankit.quicklink.mapper;

import com.ankit.quicklink.entity.Link;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LinkMapper {

    public static Link toLink(String originalUrl, String shortCode, LocalDateTime expiresAt, Integer maxClick) {
        Link link=new Link();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(expiresAt);
        link.setClicks(0);
        link.setMaxClick(maxClick);
        return link;
    }
}

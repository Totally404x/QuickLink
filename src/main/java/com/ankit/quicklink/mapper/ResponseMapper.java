package com.ankit.quicklink.mapper;

import com.ankit.quicklink.dto.LinkResponseDTO;
import com.ankit.quicklink.entity.Link;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    //public static LinkResponseDTO toResponse(String originalUrl, String shortCode, LocalDateTime createdAt, LocalDateTime expiresAt, LinkStatus status, Integer count) {
    public LinkResponseDTO toResponse(Link link) {
        LinkResponseDTO responseDTO=new LinkResponseDTO();
        responseDTO.setOriginalUrl(link.getOriginalUrl());
        responseDTO.setShortCode(link.getShortCode());
        responseDTO.setCreatedAt(link.getCreatedAt());
        responseDTO.setExpiresAt(link.getExpiresAt());
        responseDTO.setStatus(link.getStatus());
        responseDTO.setClicks(link.getClicks());
        return responseDTO;
    }
}

package com.ankit.quicklink.service;

import com.ankit.quicklink.dto.LinkAnalyticsDTO;
import com.ankit.quicklink.entity.ClickEvent;
import com.ankit.quicklink.entity.Link;
import com.ankit.quicklink.entity.LinkStatus;
import com.ankit.quicklink.mapper.LinkMapper;
import com.ankit.quicklink.repository.ClickEventRepository;
import com.ankit.quicklink.repository.DailyClickProjection;
import com.ankit.quicklink.repository.LinkRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkService {
    private final LinkRepository linkRepository;
    private final LinkMapper toLink;
    private final ClickEventRepository clickEventRepository;
    private final StringRedisTemplate redisTemplate;
    private final LinkStatusService linkStatusService;

    public LinkService(LinkRepository linkRepository, LinkMapper toLink, ClickEventRepository clickEventRepository, StringRedisTemplate redisTemplate, LinkStatusService linkStatusService) {
        this.linkRepository=linkRepository;
        this.toLink=toLink;
        this.clickEventRepository=clickEventRepository;
        this.redisTemplate=redisTemplate;
        this.linkStatusService=linkStatusService;
    }

    private String getRedisKey(String shortCode) {
        return "link:"+shortCode;
    }

    private String generateShortCode() {
        String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code= new StringBuilder();
        for(int i=0;i<6;i++) {
            int index=(int)(Math.random()*characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

    @Transactional
    public Link createLink(String originalUrl, LocalDateTime expiresAt, Integer maxClick) {
        if(expiresAt!=null && !expiresAt.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new RuntimeException("Validity Period too short or already expired.");
        }
        String shortCode;
        do{
            shortCode=generateShortCode();
        }while(linkRepository.existsByShortCode(shortCode));
        return linkRepository.save(LinkMapper.toLink(originalUrl,shortCode,expiresAt,maxClick));
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        String redisKey=getRedisKey(shortCode);
        String cachedURL=redisTemplate.opsForValue().get(redisKey);
        Link link= linkRepository.findByShortCode(shortCode).orElseThrow(() -> new RuntimeException("Entered short code does not exist."));
        String originalUrl=cachedURL!=null?cachedURL:link.getOriginalUrl();
        if(link.getStatus()==LinkStatus.DISABLED) {
            throw new RuntimeException("Link has been disabled.");
        }
        if(link.getExpiresAt()!=null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            linkStatusService.updateStatus(link,LinkStatus.EXPIRED);
            throw new RuntimeException("Link has expired.");
        }
        if(link.getMaxClick()!=null && link.getClicks()>=link.getMaxClick()) {
            linkStatusService.updateStatus(link,LinkStatus.LIMIT_REACHED);
            throw new RuntimeException("Maximum click limit has been reached for this link.");
        }

        link.setClicks(link.getClicks()+1);
        ClickEvent clickEvent=new ClickEvent();
        clickEvent.setClickedAt(LocalDateTime.now());
        clickEvent.setLink(link);
        clickEventRepository.save(clickEvent);
        redisTemplate.opsForValue().set(redisKey, originalUrl, Duration.ofMinutes(15));
        return originalUrl;
    }

    public Page<Link> getAllLinks(Pageable pageable) {
        return linkRepository.findAll(pageable);
    }

    public Link getLinkById(Long id) {
        return linkRepository.findById(id).orElseThrow(()-> new RuntimeException("Link was not found."));
    }

    public Page<Link> getLinkByOriginalUrl(String originalUrl, Pageable pageable) {
        Page<Link> links= linkRepository.findByOriginalUrlContaining(originalUrl, pageable);
        if(links.isEmpty()) {
            throw new RuntimeException("No Link exists by entered URL.");
        }
        return links;
    }

    public LinkAnalyticsDTO getlinkAnalytics(Long id) {
        if(!linkRepository.existsById(id)) {
            throw new RuntimeException("Link does not exist.");
        }
        long totalClicks= clickEventRepository.countByLinkId(id);
        return new LinkAnalyticsDTO(id, totalClicks);
    }

    public List<DailyClickProjection> getDailyClicks(Long id) {
        if (!linkRepository.existsById(id)) {
            throw new RuntimeException("Link does not exist.");
        }
        return clickEventRepository.getDailyClicks(id);
    }

    @Transactional
    public Link updateLinkUrl(Long id, String newUrl) {
        Link link= linkRepository.findById(id).orElseThrow(()-> new RuntimeException("Link does not exists."));
        link.setOriginalUrl(newUrl);
        return link;
    }

    @Transactional
    public Link updateLinkExpiry(Long id, LocalDateTime expiresAt) {
        Link link=linkRepository.findById(id).orElseThrow(()-> new RuntimeException("Link does not exists."));
        link.setExpiresAt(expiresAt);
        return link;
    }

    @Transactional
    public Link updateLinkStatus(Long id, LinkStatus status) {
        Link link=linkRepository.findById(id).orElseThrow(()-> new RuntimeException("Link does not exists."));
        link.setStatus(status);
        return link;
    }

    @Transactional
    public void deleteLink(Long id) {
        if(!linkRepository.existsById(id)) {
            throw new RuntimeException("Link does not exist.");
        }
        linkRepository.deleteById(id);
    }
}

package com.ankit.quicklink.service;

import com.ankit.quicklink.dto.LinkAnalyticsDTO;
import com.ankit.quicklink.entity.ClickEvent;
import com.ankit.quicklink.entity.Link;
import com.ankit.quicklink.entity.LinkStatus;
import com.ankit.quicklink.exception.*;
import com.ankit.quicklink.mapper.LinkMapper;
import com.ankit.quicklink.repository.ClickEventRepository;
import com.ankit.quicklink.repository.DailyClickProjection;
import com.ankit.quicklink.repository.LinkRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkService {
    private final LinkRepository linkRepository;
    private final ClickEventRepository clickEventRepository;
    private final StringRedisTemplate redisTemplate;
    private final LinkStatusService linkStatusService;
    private final PasswordEncoder passwordEncoder;

    public LinkService(LinkRepository linkRepository, ClickEventRepository clickEventRepository, StringRedisTemplate redisTemplate, LinkStatusService linkStatusService, PasswordEncoder passwordEncoder) {
        this.linkRepository=linkRepository;
        this.clickEventRepository=clickEventRepository;
        this.redisTemplate=redisTemplate;
        this.linkStatusService=linkStatusService;
        this.passwordEncoder=passwordEncoder;
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

    private Boolean passwordChecker(Link link, String password) {
        if(link.getPasswordHash()!=null) {
            if(password!=null && !passwordEncoder.matches(password, link.getPasswordHash()))
                return false;
        }
        return true;
    }

    @Transactional
    public Link createLink(String originalUrl, String customAlias, LocalDateTime scheduledAt, LocalDateTime expiresAt, String password, Boolean oneTime, Integer maxClick) {
        if(password!=null) {
            password=passwordEncoder.encode(password);
        }
        if(expiresAt!=null && !expiresAt.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new InvalidLinkException("Validity Period too short or already expired.");
        }
        if(scheduledAt!=null && scheduledAt.isBefore(LocalDateTime.now().minusSeconds(5))) {
            throw new InvalidLinkException("Schedule must use a future date/time.");
        }
        String shortCode;
        if(customAlias==null) {
            do {
                shortCode = generateShortCode();
            } while (linkRepository.existsByShortCode(shortCode));
        }
        else {
            if(linkRepository.existsByShortCode(customAlias))
                throw new LinkAlreadyExistsException("This alias is already being used; Please choose another.");
            shortCode=customAlias;
        }
        if(oneTime)
            maxClick=1;
        return linkRepository.save(LinkMapper.toLink(originalUrl,shortCode,scheduledAt,expiresAt,password, maxClick));
    }

    @Transactional
    public String getOriginalUrl(String shortCode, String password) {
        String redisKey=getRedisKey(shortCode);
        String cachedURL=redisTemplate.opsForValue().get(redisKey);
        Link link= linkRepository.findByShortCode(shortCode).orElseThrow(() -> new LinkNotFoundException("Entered short code does not exist."));
        String originalUrl=cachedURL!=null?cachedURL:link.getOriginalUrl();
        if(!passwordChecker(link, password)) {
            throw new InvalidPasswordException("Password does not match.");
        }
        if(link.getStatus()==LinkStatus.DISABLED) {
            throw new LinkDisabledException("Link has been disabled.");
        }
        if(link.getScheduledAt()!=null && link.getScheduledAt().isAfter(LocalDateTime.now())) {
            linkStatusService.updateStatus(link,LinkStatus.YET_TO_BE_ACTIVATED);
            throw new LinkNotActiveException("This link has been scheduled for activation at a later date or time.");
        }
        if(link.getExpiresAt()!=null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            linkStatusService.updateStatus(link,LinkStatus.EXPIRED);
            throw new LinkExpiredException("Link has expired.");
        }
        if(link.getMaxClick()!=null && link.getClicks()>=link.getMaxClick()) {
            linkStatusService.updateStatus(link,LinkStatus.LIMIT_REACHED);
            throw new ClickLimitReachedException("Maximum click limit has been reached for this link.");
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
        return linkRepository.findById(id).orElseThrow(()-> new LinkNotFoundException("Link was not found."));
    }

    public Page<Link> getLinkByOriginalUrl(String originalUrl, Pageable pageable) {
        Page<Link> links= linkRepository.findByOriginalUrlContaining(originalUrl, pageable);
        if(links.isEmpty()) {
            throw new LinkNotFoundException("No Link exists by entered URL.");
        }
        return links;
    }

    public LinkAnalyticsDTO getLinkAnalytics(Long id) {
        if(!linkRepository.existsById(id)) {
            throw new LinkNotFoundException("Link does not exist.");
        }
        long totalClicks= clickEventRepository.countByLinkId(id);
        return new LinkAnalyticsDTO(id, totalClicks);
    }

    public List<DailyClickProjection> getDailyClicks(Long id) {
        if (!linkRepository.existsById(id)) {
            throw new LinkNotFoundException("Link does not exist.");
        }
        return clickEventRepository.getDailyClicks(id);
    }

    @Transactional
    public boolean updateLinkUrl(Long id, String newUrl) {
        Link link= linkRepository.findById(id).orElseThrow(()-> new LinkNotFoundException("Link does not exists."));
        String key= "link:"+link.getShortCode();
        link.setOriginalUrl(newUrl);
        redisTemplate.opsForValue().set(key,newUrl);
        return true;
    }

    @Transactional
    public boolean updateLinkExpiry(Long id, LocalDateTime expiresAt) {
        Link link=linkRepository.findById(id).orElseThrow(()-> new LinkNotFoundException("Link does not exists."));
        link.setExpiresAt(expiresAt);
        return true;
    }

    @Transactional
    public boolean updateLinkStatus(Long id, LinkStatus status) {
        Link link=linkRepository.findById(id).orElseThrow(()-> new LinkNotFoundException("Link does not exists."));
        link.setStatus(status);
        return true;
    }

    @Transactional
    public boolean deleteLink(Long id) {
        Link link=linkRepository.findById(id).orElseThrow(()-> new LinkNotFoundException("Link does not exist."));
        String key="link:"+link.getShortCode();
        redisTemplate.delete(key);
        linkRepository.deleteById(id);
        if(linkRepository.existsById(id))
            return false;
        else
            return true;
    }
}

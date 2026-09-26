package com.ankit.quicklink.service;

import com.ankit.quicklink.entity.Link;
import com.ankit.quicklink.entity.LinkStatus;
import com.ankit.quicklink.repository.LinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkStatusService {
    private final LinkRepository linkRepository;

    public LinkStatusService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Link link, LinkStatus status) {
        link.setStatus(status);
        linkRepository.save(link);
    }
}

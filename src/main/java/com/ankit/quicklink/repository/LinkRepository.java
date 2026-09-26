package com.ankit.quicklink.repository;

import com.ankit.quicklink.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    Page<Link> findByOriginalUrlContaining(String originalUrl, Pageable pageable);

    boolean existsByShortCode(String shortCode);

}

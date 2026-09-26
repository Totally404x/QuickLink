package com.ankit.quicklink.repository;


import com.ankit.quicklink.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    long countByLinkId(Long id);

    @Query("""
        SELECT DATE(c.clickedAt) AS date, COUNT(c.id) AS clicks
        FROM ClickEvent c
        WHERE c.link.id = :linkId
        GROUP BY DATE(c.clickedAt)
        ORDER BY DATE(c.clickedAt)
        """)
    List<DailyClickProjection> getDailyClicks(Long linkId);
}

package com.ankit.quicklink.dto;

public class LinkAnalyticsDTO {

    private Long linkId;
    private long totalClicks;

    public LinkAnalyticsDTO() {

    }

    public LinkAnalyticsDTO(Long linkId, long totalClicks) {
        this.linkId=linkId;
        this.totalClicks=totalClicks;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }
}

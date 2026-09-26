package com.ankit.quicklink.dto;

import com.ankit.quicklink.entity.LinkStatus;

public class LinkStatusRequestDTO {

    private LinkStatus status;

    public LinkStatus getStatus() {
        return status;
    }

    public void setStatus(LinkStatus status) {
        this.status = status;
    }
}

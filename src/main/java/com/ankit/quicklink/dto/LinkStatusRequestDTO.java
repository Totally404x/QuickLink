package com.ankit.quicklink.dto;

import com.ankit.quicklink.entity.LinkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LinkStatusRequestDTO {

    @NotNull
    private LinkStatus status;

    public LinkStatus getStatus() {
        return status;
    }

    public void setStatus(LinkStatus status) {
        this.status = status;
    }
}

package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;

    public UserStatusRequest() {
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
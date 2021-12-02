package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTimezoneResponse {
    private long offset;
    private String offsetName;
}

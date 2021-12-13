package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterPageActionBean {
    private int id;
    private String name;
    private int status;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;
}

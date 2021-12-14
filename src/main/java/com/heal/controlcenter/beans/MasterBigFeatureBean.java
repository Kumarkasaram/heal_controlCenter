package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MasterBigFeatureBean {
    private int id;
    private String name;
    private String identifier;
    private String description;
    private int uiVisible;
    private int status;
    private String dashboardName;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;

}

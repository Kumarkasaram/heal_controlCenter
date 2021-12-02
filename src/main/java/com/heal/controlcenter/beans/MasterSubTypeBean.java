package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class MasterSubTypeBean {
    private int id;
    private String name;
    private int mstTypeId;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;
    private int accountId;
    private String description;
    private int isCustom;
    private int status;
}
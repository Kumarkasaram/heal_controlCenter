package com.heal.controlcenter.beans;

import lombok.Data;

import java.util.Date;

@Data
public class MasterTimezoneBean {
    private int id;
    private String timeZoneId;
    private int timeOffset;
    private Date createdTime;
    private Date updatedTime;
    private String userDetailsId;
    private int accountId;
    private String offsetName;
    private int status;
    private String abbreviation;
}

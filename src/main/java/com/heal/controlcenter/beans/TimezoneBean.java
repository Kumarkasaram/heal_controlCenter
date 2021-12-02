package com.heal.controlcenter.beans;

import lombok.Data;

/**
 * @author Sourav Suman : 08/04/2019
 */
@Data
public class TimezoneBean {
    private int id;
    private String timeZoneId;
    private long offset;
    private String userDetailsId;
    private int accountId;
    private int status;
}

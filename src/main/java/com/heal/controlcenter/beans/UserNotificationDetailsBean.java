package com.heal.controlcenter.beans;

import lombok.Data;

/**
 * @author Sourav Suman
 */
@Data
public class UserNotificationDetailsBean {

    private int smsEnabled;
    private int emailEnabled;
    private int forensicEnabled;
    private int accountId;
    private String applicableUserId;
}

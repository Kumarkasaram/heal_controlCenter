package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  DefaultNotificationPreferences implements Serializable {
    private int id;
    private int applicationId;
    private int notificationTypeId;
    private int signalTypeId;
    private int signalSeverityId;
    private int accountId;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;

}

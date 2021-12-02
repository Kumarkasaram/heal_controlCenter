package com.heal.controlcenter.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonIgnoreProperties({"createdTime", "updatedTime", "applicationId", "userId"})
public class UserNotificationPreferenceBean {

    private int signalTypeId;
    private String signalType;

    private int signalSeverityId;
    private String signalSeverity;

    private int notificationTypeId;
    private String notificationType;

    private Timestamp createdTime;
    private Timestamp updatedTime;

    private int applicationId;

    private String userId;
}


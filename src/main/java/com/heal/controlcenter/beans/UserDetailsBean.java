package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsBean {
    private String userId;
    private String userName;
    private String role;
    private String userProfile;
    private int status;
    private Long updatedOn;
    private String updatedBy;
    private int emailNotification;
    private int smsNotification;
    private int forensicNotification;
}


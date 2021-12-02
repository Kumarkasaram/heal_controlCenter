package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoBean {
    private int mysqlId;
    // Here `id` is user identifier
    private String id;
    private String userName;
    private String firstName;
    private String lastName;
    private String emailId;
    private String contactNumber;
    private int status;
    private int roleId;
    private int profileId;
    private Boolean profileChange;
    private String userDetailsId;
    private String accessDetailsJSON;
    private AccessDetailsBean accessDetails;
    private boolean editInKeycloak;
    private int isTimezoneMychoice;
    private int isNotificationsTimezoneMychoice;
    private KeycloakUserBean keycloakRollbackUserDetails;
    private int smsEnabled;
    private int emailEnabled;
    private String createdTime;
    private String lastLoginTime;
    private int notificationPreferenceId;
}

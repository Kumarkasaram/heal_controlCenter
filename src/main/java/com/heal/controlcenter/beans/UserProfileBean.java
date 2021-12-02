package com.heal.controlcenter.beans;

import lombok.Data;

import java.util.Set;

@Data
public class UserProfileBean {
    private int userProfileId;
    private String userProfileName;
    private String role;
    private Set<String> accessibleFeatures;
}

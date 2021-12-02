package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class UserAttributesBean {

    private int roleId;
    private int accessProfileId;
    private String roleName;
    private String accessProfileName;
}

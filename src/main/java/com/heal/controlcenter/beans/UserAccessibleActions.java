package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccessibleActions {

    private int roleId;
    private int profileId;
    private int isActiveDirectory;
    private String role;
    private String profile;
    private List<String> allowedActions = new ArrayList<>();
}

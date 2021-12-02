package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationBean {

    private int id;
    private String name;
    private String identifier;
    private List<ControllerBean> addServices;
    private TimezoneBean timezone;
    private List<Integer> deleteServiceIds;
    private int accountId;
    private String userId;

}

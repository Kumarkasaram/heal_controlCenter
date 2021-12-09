package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class CompInstClusterMappingBean {

    private int id;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;
    private int accountId;
    private int compInstanceId;
    private int clusterId;

}


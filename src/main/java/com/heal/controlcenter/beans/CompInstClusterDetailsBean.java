package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class CompInstClusterDetailsBean {

    private int instanceId;
    private int status;
    private int commonVersionId;
    private String commonVersionName;
    private int compId;
    private int mstComponentTypeId;
    private int compVersionId;
    private String instanceName;
    private int hostId;
    private String hostName;
    private int isCluster;
    private String identifier;
    private String componentName;
    private String componentTypeName;
    private String componentVersionName;
    private String hostAddress;
    private int supervisorId;
    private String userDetailsId;
    private String createdTime;
    private String updatedTime;

}


package com.heal.controlcenter.beans;

import lombok.Data;

@Data
public class CompInstClusterDetails {
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

//    public InstanceDetails getInstanceDetails() throws ParseException {
//        InstanceDetails instanceDetails = new InstanceDetails();
//        instanceDetails.setName(instanceName);
//        instanceDetails.setId(instanceId);
//        instanceDetails.setType(componentName);
//        instanceDetails.setVersion(componentVersionName);
//        instanceDetails.setStatus(status);
//        instanceDetails.setIdentifier(identifier);
//        instanceDetails.setAddress(hostAddress);
//        instanceDetails.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createdTime).getTime());
//        instanceDetails.setLastModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(updatedTime).getTime());
//        KeyCloakUserDetails keyCloakUserDetails = MasterCache.getKeycloakUserDetails(userDetailsId);
//        if (Objects.isNull(keyCloakUserDetails)){
//            instanceDetails.setCreatedBy("");
//            instanceDetails.setLastModifiedBy("");
//        }else{
//            instanceDetails.setCreatedBy(keyCloakUserDetails.getUsername());
//            instanceDetails.setLastModifiedBy(keyCloakUserDetails.getUsername());
//        }
//        return instanceDetails;
//    }
}

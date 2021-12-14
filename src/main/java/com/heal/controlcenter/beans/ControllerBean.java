package com.heal.controlcenter.beans;

import com.heal.controlcenter.pojo.ServiceConfigPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ControllerBean {

    private int id;
    private String appId;
    private String name;
    private String identifier;
    private int status;
    private int accountId;
    private int controllerTypeId;
    private long timeOffset;
    private String lastModifiedBy;
    private String createdTime;
    private String updatedTime;

    private List<ServiceConfigPojo> serviceDetails = new ArrayList<>();
    private List<Integer> percentiles = new ArrayList<>();
    private int pluginSuppressionInterval;
    private boolean pluginWhitelisted;

    public ControllerBean (int accountId,String appId,String name,int status,int controllerTypeId) {
      this.accountId= accountId;
      this.appId = appId;
      this.name =name;
      this.status =status;
      this.controllerTypeId =controllerTypeId;
    }
}

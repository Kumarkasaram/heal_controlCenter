package com.heal.controlcenter.beans;

import com.heal.controlcenter.enums.SetupTypes;
import lombok.Data;

@Data
public class InstancesBean {

    private Integer accountId;
    private Integer actionTypeId;
    private String serviceId;
    private String typeName;
    private SetupTypes type;
    private String user;
    private String accountIdentifier;

}

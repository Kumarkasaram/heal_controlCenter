package com.heal.controlcenter.pojo;

import com.appnomic.appsone.common.enums.DiscoveryStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetConnectionPojo {

    private int sourceServiceId;
    private String sourceServiceName;
    private String sourceServiceIdentifier;
    private int destinationServiceId;
    private String destinationServiceName;
    private String destinationServiceIdentifier;
    private String process;
    private DiscoveryStatus status;
    private long lastDiscoveryRunTime;

}

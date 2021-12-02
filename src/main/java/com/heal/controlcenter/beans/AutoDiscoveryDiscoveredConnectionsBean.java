package com.heal.controlcenter.beans;

import com.appnomic.appsone.common.enums.DiscoveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoDiscoveryDiscoveredConnectionsBean {

    private int id;
    @JsonIgnore
    private String hostIdentifier;
    private String sourceIdentifier;
    private String destinationIdentifier;
    private long lastUpdatedTime;
    private int isDiscovery;
    private DiscoveryStatus discoveryStatus;

}

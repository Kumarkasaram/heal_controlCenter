package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewApplicationServiceMappingBean {

    private Integer applicationId;
    private String applicationName;
    private String applicationIdentifier;
    private Integer serviceId;
    private String serviceName;
    private String serviceIdentifier;
    private Integer accountId;

}

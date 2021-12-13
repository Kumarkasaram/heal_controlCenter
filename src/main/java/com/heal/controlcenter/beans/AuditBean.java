package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AuditBean {
    int bigFeatureId;
    int appId;
    int svcId;
    int pageActionId;
    String updatedBy;
    String auditTime;
    String operationType;
    String auditData;
}

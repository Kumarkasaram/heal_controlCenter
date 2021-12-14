package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AuditTrailPojo {
    String applicationName ;
    String ServiceName;
    String activityType;
    String subActivityType;
    String operationType;
    String updatedBy;
    Long auditTime;
    Map<String, Map<String, String>> value;
}

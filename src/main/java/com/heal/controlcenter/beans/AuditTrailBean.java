package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@Slf4j
@NoArgsConstructor
public class AuditTrailBean {
    private List<Integer> bigFeatureIds;
    private List<Integer> appIds;
    private List<Integer> serviceIds;
    private Integer accountId;
    private String userId;
    private long fromTime;
    private long toTime;
    private  String timeZone;
    private  String defaultTimeZone;
}

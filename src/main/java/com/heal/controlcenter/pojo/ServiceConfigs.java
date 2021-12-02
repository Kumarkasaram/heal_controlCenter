package com.heal.controlcenter.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceConfigs {

    private int startCollectionInterval;
    private int endCollectionInterval;
    private int sorPersistence;
    private int sorSuppression;
    private int norPersistence;
    private int norSuppression;
}

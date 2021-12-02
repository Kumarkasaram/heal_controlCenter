package com.heal.controlcenter.beans;

import com.heal.controlcenter.pojo.ServiceConfigs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfigurationBean {
    private int id;
    private int serviceId;
    private int accountId;
    private String userDetailsId;
    private String createdTime;
    private String updatedTime;
    private int startCollectionInterval;
    private int endCollectionInterval;
    private int sorPersistence;
    private int sorSuppression;
    private int norPersistence;
    private int norSuppression;

    public ServiceConfigs getConfigs() {
        return ServiceConfigs.builder()
                .startCollectionInterval(this.startCollectionInterval)
                .endCollectionInterval(this.endCollectionInterval)
                .sorPersistence(this.sorPersistence)
                .sorSuppression(this.sorSuppression)
                .norPersistence(this.norPersistence)
                .norSuppression(this.norSuppression)
                .build();
    }

}
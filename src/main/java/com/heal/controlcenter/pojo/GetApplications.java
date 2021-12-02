package com.heal.controlcenter.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetApplications {

    private int id;
    private String name;
    private String identifier;
    private long lastModifiedOn;
    private String lastModifiedBy;
    private List<ServiceClusterDetails> services;

    @Data
    @Builder
    public static class ServiceClusterDetails {

        private int id;
        private String name;
        private String identifier;
        private List<ClusterComponentDetails> hostCluster;
        private List<ClusterComponentDetails> componentCluster;

    }

    @Data
    public static class ClusterComponentDetails {

        private int id;
        private String name;
        private String identifier;
        private int componentId;
        private String componentName;
        private int componentVersionId;
        private String componentVersionName;
        private int commonVersionId;
        private String commonVersionName;
        private int componentTypeId;
        private String componentTypeName;

    }
}

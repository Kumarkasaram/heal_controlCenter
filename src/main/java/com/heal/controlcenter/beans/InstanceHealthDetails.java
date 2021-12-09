package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceHealthDetails {
    private int id;
    private String instanceName;
    private String type;
    private Set<String> services;
    private String host;
    private Long lastPostedTime;
    private int dataPostStatus;
}

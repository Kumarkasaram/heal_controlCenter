package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessDetails {

    private List<String> applicationIdentifiers;
    private List<Integer> applicationIds;
    private List<Integer> serviceIds;
    private List<String> serviceIdentifiers;
    private List<Integer> transactionIds;
    private List<AgentBean> agents;
    private List<ViewApplicationServiceMappingBean> applicationServiceMappingBeans;
}

package com.heal.controlcenter.pojo;

import com.heal.controlcenter.beans.AgentBean;
import com.heal.controlcenter.beans.ViewApplicationServiceMappingBean;
import lombok.Data;

import java.util.List;

@Data
public class UserAccessDetailsPojo {

    private List<String> applicationIdentifiers;
    private List<Integer> applicationIds;
    private List<Integer> serviceIds;
    private List<String> serviceIdentifiers;
    private List<Integer> transactionIds;
    private List<AgentBean> agents;
    private List<ViewApplicationServiceMappingBean> applicationServiceMappingBeans;

}


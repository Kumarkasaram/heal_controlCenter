package com.heal.controlcenter.pojo;

import com.heal.controlcenter.beans.ControllerBean;
import com.heal.controlcenter.beans.UserAccessDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessInfo {

    private List<ControllerBean> accessibleApplications;
    private UserAccessDetails userAccessDetails;

}

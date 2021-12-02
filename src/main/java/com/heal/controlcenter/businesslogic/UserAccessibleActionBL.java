package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UserAccessibleActions;
import com.heal.controlcenter.beans.UserAttributesBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserAccessibleActionBL implements BusinessLogic<String, UserAttributesBean, UserAccessibleActions> {

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    UserDao userAccessDataDao;

    @Override
    public UtilityBean<String> clientValidation(String requestBody, String... requestParams) throws ClientException {
        String userId;
        try {
            userId = commonUtils.getUserId(requestParams[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while fetching userId. Reason: {}", e.getMessage(), e);
            throw new ClientException("Error while fetching userId from the Authorization token");
        }

        if(null == userId) {
            log.error("Invalid Authorization token. Reason: UserId is NULL.");
            throw new ClientException("Invalid Authorization token");
        }

        return UtilityBean.<String>builder()
                .pojoObject(userId)
                .build();
    }

    @Override
    public UserAttributesBean serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        String userId = utilityBean.getPojoObject();

        UserAttributesBean userAttributesBean;
        try {
            userAttributesBean = userAccessDataDao.getRoleProfileInfoForUserId(userId);
        } catch (ControlCenterException e) {
            throw new ServerException(e.getMessage());
        }

        if(null == userAttributesBean) {
            log.error("User details unavailable for userId [{}]", userId);
            throw new ServerException("User details unavailable");
        }

        return userAttributesBean;
    }

    @Override
    public UserAccessibleActions process(UserAttributesBean userAttributesBean) throws DataProcessingException {
        List<String> allowedActions;
        try {
            allowedActions = userAccessDataDao.getUserAccessibleActions(userAttributesBean.getAccessProfileId());
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }

        return UserAccessibleActions.builder()
                .profileId(userAttributesBean.getAccessProfileId())
                .profile(userAttributesBean.getAccessProfileName())
                .roleId(userAttributesBean.getRoleId())
                .role(userAttributesBean.getRoleName())
                .isActiveDirectory(0)
                .allowedActions(allowedActions)
                .build();
    }
}

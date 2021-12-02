package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UserDetailsBean;
import com.heal.controlcenter.beans.UserNotificationDetailsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.NotificationPreferencesDataDao;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sourav Suman - 28-10-2021
 */
@Service
@Slf4j
public class UserDetailsBL implements BusinessLogic<String, String, List<UserDetailsBean>>{

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    UserDao userDao;
    @Autowired
    NotificationPreferencesDataDao notificationPreferencesDataDao;

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
    public String serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        return  null;
    }

    @Override
    public List<UserDetailsBean> process(String bean) throws DataProcessingException {
        try {
            log.info("fetching users detail");
            List<UserDetailsBean> userDetailsBeanList = userDao.getNonSuperUsers();

            if(userDetailsBeanList == null || userDetailsBeanList.isEmpty()){
                log.info("User list fetched from schema is empty.");
                return Collections.emptyList();
            }

            Map<String, UserNotificationDetailsBean> userNotificationDetailsBeanMap = notificationPreferencesDataDao.getEmailSmsForensicNotificationStatusForUsers();

            return userDetailsBeanList.stream().sorted(Comparator.comparing(UserDetailsBean::getUpdatedOn))
                    .map(user -> {
                        UserNotificationDetailsBean detailsBean = null;
                        if(userNotificationDetailsBeanMap.containsKey(user.getUserId())) {
                            detailsBean = userNotificationDetailsBeanMap.get(user.getUserId());
                        }
                        String updatedBy = this.getUserName(user.getUpdatedBy());
                        user.setUpdatedBy(updatedBy);
                        user.setEmailNotification(detailsBean == null ? 0 : detailsBean.getEmailEnabled());
                        user.setSmsNotification(detailsBean == null ? 0 : detailsBean.getSmsEnabled());
                        user.setForensicNotification(detailsBean == null ? 0 : detailsBean.getForensicEnabled());
                        return user;
                    }).collect(Collectors.toList());
        } catch (ControlCenterException e) {
            log.error("Error occured while forming response object", e);
            throw new DataProcessingException(e.getMessage());
        }
    }

    private String getUserName(String userId){
        try {
            return userDao.getUsernameFromIdentifier(userId);
        } catch (ControlCenterException e) {
            log.error("Error occurred while getting user name for identifier [{}]", userId, e);
            return null;
        }
    }
}

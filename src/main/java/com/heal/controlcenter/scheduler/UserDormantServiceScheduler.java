package com.heal.controlcenter.scheduler;

import com.appnomic.appsone.keycloak.KeycloakConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.beans.KeycloakUserBean;
import com.heal.controlcenter.beans.UserInfoBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserDormantServiceScheduler {

    @Autowired
    private UserDao userDetailsDao;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.dormant.creation.time.days:30}")
    private int userDormantAfterCreationDateWithoutLogin;

    @Value("${user.dormant.login.time.days:90}")
    private int userDormantAfterLastLoginDate;


    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    public void findDormantUser() {
        try {
            List<UserInfoBean> activeUsers = userDetailsDao.getActiveUsers();
            UserInfoBean superAdmin = userDetailsDao.getSuperAdmin();
            long scheduledDateInTimeMillis = dateTimeUtil.getDateInGMT(new DateTime().withTimeAtStartOfDay().getMillis()).getTime();
            log.info("Dormancy scheduler triggered");

            for (UserInfoBean user : activeUsers) {
                boolean dormantUserFound = false;

                log.info("Dormancy set is {} days since user creation and {} days since last login", userDormantAfterCreationDateWithoutLogin, userDormantAfterLastLoginDate);

                if (user.getLastLoginTime() != null && user.getLastLoginTime().trim().length() > 0) {
                    long gmtLastLoginTime = Timestamp.valueOf(user.getLastLoginTime()).getTime();

                    log.debug("Difference between Last login time and scheduled time is: {} in milliseconds", (scheduledDateInTimeMillis - gmtLastLoginTime));

                    if (TimeUnit.DAYS.toMillis(userDormantAfterLastLoginDate) < (scheduledDateInTimeMillis - gmtLastLoginTime)) {
                        user.setStatus(0);
                        log.info("Last login crossed configured time for user: {}", user.getUserName());
                        dormantUserFound = true;
                    }
                } else if (user.getCreatedTime() != null) {
                    long gmtCreatedTime = dateTimeUtil.getGMTToEpochTime(user.getCreatedTime());

                    log.debug("Difference between Created time and scheduled time is: {} in milliseconds", (scheduledDateInTimeMillis - gmtCreatedTime));

                    if (TimeUnit.DAYS.toMillis(userDormantAfterCreationDateWithoutLogin) < (scheduledDateInTimeMillis - gmtCreatedTime)) {
                        user.setStatus(0);
                        log.info("{}'s login/password expired without login done.", user.getUserName());
                        dormantUserFound = true;
                    }
                }

                if (dormantUserFound) {
                    updateUserStatusInKeyCloakAndHeal(user, superAdmin);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while user dormant scheduler execute", e);
        }
    }

    public void updateUserStatusInKeyCloakAndHeal(UserInfoBean dormantUser, UserInfoBean superAdmin) throws ControlCenterException {
        try {
            log.info("{} is now in dormant state by system admin {}", dormantUser.getUserName(), superAdmin.getUserName());
            disableUserFromKeycloak(dormantUser);
            userDetailsDao.updateUserStatusToInactive(dormantUser.getId(), superAdmin.getId());
        } catch (Exception e) {
            log.error(e.getCause().getCause().getMessage() + "Details: ", e);
            throw new ControlCenterException(e.getCause().getCause().getMessage());
        }
    }

    private void disableUserFromKeycloak(UserInfoBean user) throws ControlCenterException {
        try {
            KeycloakConnectionManager.editKeycloakUser(
                    objectMapper.writeValueAsString(KeycloakUserBean.builder()
                            .enabled(String.valueOf(user.getStatus() == 1))
                            .email(user.getEmailId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .username(user.getUserName()).build()), user.getId());
        } catch (IOException e) {
            log.error("Edit operation failed in keycloak.", e);
            throw new ControlCenterException("Edit operation failed in keycloak.");
        }
    }
}

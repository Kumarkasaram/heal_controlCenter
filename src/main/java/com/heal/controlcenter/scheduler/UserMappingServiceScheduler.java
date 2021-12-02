package com.heal.controlcenter.scheduler;

import com.heal.controlcenter.dao.mysql.SignalNotificationPreferenceDao;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class UserMappingServiceScheduler {

    @Autowired
    SignalNotificationPreferenceDao notificationPreferencesDataService;
    @Autowired
    UserDao userDao;

    @Scheduled(initialDelay = 1000, fixedRate = 300000)
    public void clearRedundantUsersFromHeal() {
        try {
            log.info("clearRedundantUsersFromHeal() invoked");
            List<String> userDetails = userDao.getUserIdentifiers();
            if (!userDetails.isEmpty()) {
                List<String> keycloakUserIds = userDao.getKeycloakUserIdentifiers();
                for (String userId : userDetails) {
                    if (keycloakUserIds == null || !keycloakUserIds.contains(userId)) {
                        log.info("User [{}] exists in HEAL, but unavailable in keycloak", userId);
                        deleteData(userId);
                        log.info("User [{}] deleted from HEAL as this user is unavailable in keycloak", userId);
                    }
                }
            }
        } catch (ControlCenterException e) {
            log.error("Error occurred while user mapping scheduler execute", e);
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void deleteData(String userId) throws ControlCenterException {
        try {
            notificationPreferencesDataService.removeNotificationDetailsForUser(userId);
            notificationPreferencesDataService.removeUserNotificationPreferencesForUser(userId);
            notificationPreferencesDataService.removeForensicNotificationPreferencesForUser(userId);
            userDao.deleteUserAttributesAndAccessDetails(userId);
        } catch (Exception e) {
            log.error("Exception encountered when deleting redundant users in scheduler. Details: {}", e.getMessage(), e);
            throw new ControlCenterException(e.getCause().getMessage());
        }
    }
}


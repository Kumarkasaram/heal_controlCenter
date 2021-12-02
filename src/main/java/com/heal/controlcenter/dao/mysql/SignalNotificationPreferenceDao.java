package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SignalNotificationPreferenceDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void removeNotificationDetailsForUser(String userId) throws ControlCenterException {
        try {
            String query = "DELETE FROM user_notifications_details where applicable_user_id = ?";
            jdbcTemplate.update(query, new Object[] {userId});
        }catch (Exception e){
            log.error("Error in deleting notification details for user {}. Details: {}, Stack trace: {}", userId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException(String.format("Error in deleting notification details for user: %s", userId));
        }
    }

    public void removeForensicNotificationPreferencesForUser(String userId) throws ControlCenterException {
        try {
            String query = "DELETE FROM user_forensic_notification_mapping where applicable_user_id = ?";
            jdbcTemplate.update(query, new Object[] {userId});
        }catch (Exception e){
            log.error("Error in deleting forensic notification details for user {}. Details: {}, Stack trace: {}", userId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException(String.format("Error in deleting forensic notification details for user: %s", userId));
        }
    }

    public void removeUserNotificationPreferencesForUser(String userId) throws ControlCenterException {
        try {
            String query = "DELETE from user_notification_mapping where applicable_user_id = ?";
            jdbcTemplate.update(query, userId);
        }catch (Exception e){
            log.error("Error in deleting forensic notification for user {}. Details: {}, Stack trace: {}", userId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException(String.format("Error in deleting forensic notification for user: %s", userId));
        }
    }
}

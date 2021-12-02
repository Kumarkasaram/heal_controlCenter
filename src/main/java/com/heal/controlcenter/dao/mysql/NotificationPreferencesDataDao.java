package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.UserNotificationDetailsBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sourav Suman - 28-10-2021
 */

@Slf4j
@Repository
public class NotificationPreferencesDataDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<String, UserNotificationDetailsBean> getEmailSmsForensicNotificationStatusForUsers() throws ControlCenterException {
        String query = "select u.applicable_user_id applicableUserId, u.is_sms_enabled smsEnabled, " +
                "u.is_email_enabled emailEnabled, u.is_forensic_enabled forensicEnabled from user_notifications_details u";
        try {
            return jdbcTemplate.query(query, rs -> {
                Map<String, UserNotificationDetailsBean> userNotificationDetailsBeanMap = new HashMap<>();
                while (rs.next()) {
                    UserNotificationDetailsBean userNotificationDetailsBean = new UserNotificationDetailsBean();
                    userNotificationDetailsBean.setSmsEnabled(rs.getInt("smsEnabled"));
                    userNotificationDetailsBean.setApplicableUserId(rs.getString("applicableUserId"));
                    userNotificationDetailsBean.setEmailEnabled(rs.getInt("emailEnabled"));
                    userNotificationDetailsBean.setForensicEnabled(rs.getInt("forensicEnabled"));
                    userNotificationDetailsBeanMap.put(rs.getString("applicableUserId"), userNotificationDetailsBean);
                }
                return userNotificationDetailsBeanMap;
            });
        } catch (Exception e) {
            log.error("Exception encountered while fetching user notification details.", e);
            throw new ControlCenterException("Exception encountered while fetching user notification details.");
        }
    }
}

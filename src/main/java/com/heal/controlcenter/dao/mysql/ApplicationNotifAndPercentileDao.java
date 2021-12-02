package com.heal.controlcenter.dao.mysql;


import com.heal.controlcenter.beans.ApplicationPercentilesBean;
import com.heal.controlcenter.beans.DefaultNotificationPreferences;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class ApplicationNotifAndPercentileDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int[] addDefaultNotificationPreferences(List<DefaultNotificationPreferences> defaultNotificationPreferences) throws ControlCenterException {
        String query = "INSERT INTO application_notification_mapping (application_id,notification_type_id,signal_type_id,signal_severity_id,account_id,created_time, updated_time, user_details_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            log.debug("adding default preferences.");
            return jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, defaultNotificationPreferences.get(i).getApplicationId());
                    ps.setInt(2, defaultNotificationPreferences.get(i).getNotificationTypeId());
                    ps.setInt(3, defaultNotificationPreferences.get(i).getSignalTypeId());
                    ps.setInt(4, defaultNotificationPreferences.get(i).getSignalSeverityId());
                    ps.setInt(5, defaultNotificationPreferences.get(i).getAccountId());
                    ps.setString(6, defaultNotificationPreferences.get(i).getCreatedTime());
                    ps.setString(7, defaultNotificationPreferences.get(i).getUpdatedTime());
                    ps.setString(8, defaultNotificationPreferences.get(i).getUserDetailsId());

                }

                public int getBatchSize() {
                    return defaultNotificationPreferences.size();
                }
            });
        } catch (Exception ex) {
            log.error("Error in adding default notification preferences. Details: ", ex);
            throw new ControlCenterException("Error in adding default notification preferences.");
        }
    }

    public int[] addApplicationPercentiles(List<ApplicationPercentilesBean> applicationPercentilesBeans) throws ControlCenterException {
        String query = "INSERT INTO `application_percentiles` (account_id, application_id, created_time, updated_time, " +
                "user_details_id, display_name, percentile_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            log.debug("adding application percentiles");
            return jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, applicationPercentilesBeans.get(i).getAccountId());
                    ps.setInt(2, applicationPercentilesBeans.get(i).getApplicationId());
                    ps.setString(3, applicationPercentilesBeans.get(i).getCreatedTime());
                    ps.setString(4, applicationPercentilesBeans.get(i).getUpdatedTime());
                    ps.setString(5, applicationPercentilesBeans.get(i).getUserDetailsId());
                    ps.setString(6, applicationPercentilesBeans.get(i).getDisplayName());
                    ps.setInt(7, applicationPercentilesBeans.get(i).getPercentileValue());
                }

                public int getBatchSize() {
                    return applicationPercentilesBeans.size();
                }

            });
        } catch (Exception ex) {
            log.error("Error in adding application percentiles. Details: ", ex);
            throw new ControlCenterException("Error in adding application percentiles");
        }
    }
}

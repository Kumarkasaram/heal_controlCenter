package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.NotificationSettingsBean;
import com.heal.controlcenter.beans.SMSDetailsBean;
import com.heal.controlcenter.beans.SMSParameterBean;
import com.heal.controlcenter.beans.SMTPDetailsBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class NotificationsDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<NotificationSettingsBean> getNotificationSetting(Integer accountId) {
        String query = "SELECT notification_type_id typeId, no_of_minutes durationInMin, account_id accountId, user_details_id lastModifiedBy, created_time createdTime, updated_time updatedTime from notification_settings WHERE account_id = " + accountId;
        try {
            log.debug("getting notification settings.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(NotificationSettingsBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching notification settings from 'notification_settings' table for accountId [{}]. Details: {}", accountId, e.getMessage());
        }
        return Collections.emptyList();
    }

    public int[] addNotificationSettings(List<NotificationSettingsBean> settingsList) throws ControlCenterException {
        String query = "INSERT INTO notification_settings (notification_type_id, no_of_minutes, account_id, created_time, updated_time, user_details_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            log.debug("adding notification settings.");
            return jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    NotificationSettingsBean settings = settingsList.get(i);
                    ps.setInt(1, settings.getTypeId());
                    ps.setInt(2, settings.getDurationInMin());
                    ps.setInt(3, settings.getAccountId());
                    ps.setString(4, settings.getCreatedTime());
                    ps.setString(5, settings.getUpdatedTime());
                    ps.setString(6, settings.getLastModifiedBy());
                }

                @Override
                public int getBatchSize() {
                    return settingsList.size();
                }
            });
        } catch (Exception e) {
            log.error("Exception encountered while adding notification settings to 'notification_settings' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while adding notification settings to account.");
        }
    }

    public void updateNotificationSetting(List<NotificationSettingsBean> settingsList) throws ControlCenterException {
        String query = "UPDATE notification_settings SET no_of_minutes=?, updated_time=?, user_details_id=? WHERE account_id=? AND notification_type_id=?";
        try {
            log.debug("updating notification settings.");
            jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    NotificationSettingsBean settings = settingsList.get(i);
                    ps.setInt(1, settings.getDurationInMin());
                    ps.setString(2, settings.getUpdatedTime());
                    ps.setString(3, settings.getLastModifiedBy());
                    ps.setInt(4, settings.getAccountId());
                    ps.setInt(5, settings.getTypeId());
                }

                @Override
                public int getBatchSize() {
                    return settingsList.size();
                }
            });
        } catch (Exception e) {
            log.error("Exception encountered while updating notification settings in 'notification_settings' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while updating notification settings for the account.");
        }
    }

    public SMSDetailsBean getSMSDetails(int accountId) {
        String query = "SELECT id, address, port, country_code countryCode, protocol_id protocolId, " +
                "http_method httpMethod, http_relative_url httpRelativeUrl, account_id accountId, " +
                "post_data postData, post_data_flag postDataFlag, status, is_multi_request isMultiRequest FROM sms_details " +
                "WHERE account_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(SMSDetailsBean.class), accountId);
        } catch (EmptyResultDataAccessException e) {
            log.info("SMS details unavailable for accountId [{}]", accountId);
        } catch (Exception e) {
            log.error("Error occurred while fetching SMS details for accountId [{}]. Details: ", accountId, e);
        }
        return null;
    }

    public List<SMSParameterBean> getSMSParameters(int id) {
        String query = "SELECT id, parameter_name parameterName, parameter_value parameterValue, " +
                "parameter_type_id parameterTypeId , sms_details_id smsDetailsId, " +
                "user_details_id userDetailsId, created_time createdTime, updated_time updatedTime, is_placeholder isPlaceholder FROM sms_parameters WHERE " +
                "sms_details_id = " + id;
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(SMSParameterBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching SMS parameters from 'sms_parameters' table. Details: ", e);
        }
        return Collections.emptyList();
    }

    public void updateSMSDetails(SMSDetailsBean smsDetailsBean) throws ControlCenterException {
        String query = "UPDATE sms_details SET address = ?, port = ?, country_code = ?, protocol_id = ?, " +
                "http_method = ?, http_relative_url = ?, user_details_id = ?, post_data = ?, post_data_flag = ?, updated_time = ?, " +
                "is_multi_request = ? WHERE account_id = " + smsDetailsBean.getAccountId();
        try {
            log.debug("updating SMS configurations.");
            jdbcTemplate.update(query, smsDetailsBean.getAddress(), smsDetailsBean.getPort(), smsDetailsBean.getCountryCode(),
                    smsDetailsBean.getProtocolId(), smsDetailsBean.getHttpMethod(), smsDetailsBean.getHttpRelativeUrl(), smsDetailsBean.getLastModifiedBy(),
                    smsDetailsBean.getPostData(), smsDetailsBean.getPostDataFlag(), smsDetailsBean.getUpdatedTime(), smsDetailsBean.getIsMultiRequest());
        } catch (Exception e) {
            log.error("Exception encountered while updating SMS configurations in 'sms_details' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while updating SMS configurations for the account.");
        }
    }

    public void addSMSParameter(List<SMSParameterBean> addSMSParametersList) throws ControlCenterException {
        String query = "INSERT INTO sms_parameters (parameter_name, parameter_value, parameter_type_id, sms_details_id,  " +
                "user_details_id, created_time, updated_time, is_placeholder)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            log.debug("adding SMS parameters.");
            jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    SMSParameterBean parameter = addSMSParametersList.get(i);
                    ps.setString(1, parameter.getParameterName());
                    ps.setString(2, parameter.getParameterValue());
                    ps.setInt(3, parameter.getParameterTypeId());
                    ps.setInt(4, parameter.getSmsDetailsId());
                    ps.setString(5, parameter.getLastModifiedBy());
                    ps.setString(6, parameter.getCreatedTime());
                    ps.setString(7, parameter.getUpdatedTime());
                    ps.setInt(8, parameter.getIsPlaceholder());
                }

                @Override
                public int getBatchSize() {
                    return addSMSParametersList.size();
                }
            });
        } catch (Exception e) {
            log.error("Exception encountered while adding SMS parameters to 'sms_parameters' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while adding SMS parameters to account.");
        }
    }

    public void updateSMSParameters(List<SMSParameterBean> modifySMSParametersList) throws ControlCenterException {
        String query = "UPDATE sms_parameters SET parameter_name=?, parameter_value=?, parameter_type_id=?, " +
                "updated_time=?, is_placeholder=?, user_details_id=? WHERE id=?";
        try {
            log.debug("updating SMS parameters.");
            jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    SMSParameterBean parameter = modifySMSParametersList.get(i);
                    ps.setString(1, parameter.getParameterName());
                    ps.setString(2, parameter.getParameterValue());
                    ps.setInt(3, parameter.getParameterTypeId());
                    ps.setString(4, parameter.getUpdatedTime());
                    ps.setInt(5, parameter.getIsPlaceholder());
                    ps.setString(6, parameter.getLastModifiedBy());
                    ps.setInt(7, parameter.getId());
                }

                @Override
                public int getBatchSize() {
                    return modifySMSParametersList.size();
                }
            });
        } catch (Exception e) {
            log.error("Exception encountered while updating SMS parameters in 'sms_parameters' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while updating SMS parameters for account.");
        }
    }

    public void deleteSmsParameters(List<SMSParameterBean> deleteSMSParametersList) throws ControlCenterException {
        String query = "DELETE FROM sms_parameters WHERE id=?";
        try {
            log.debug("deleting SMS parameters.");
            jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    SMSParameterBean parameter = deleteSMSParametersList.get(i);
                    ps.setInt(1, parameter.getId());
                }

                @Override
                public int getBatchSize() {
                    return deleteSMSParametersList.size();
                }
            });
        } catch (Exception e) {
            log.error("Exception encountered while deleting SMS parameters to 'sms_parameters' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while deleting SMS parameters from account.");
        }
    }

    public SMTPDetailsBean getSMTPDetails(Integer accountId) {
        String query = "SELECT id, address, port, username, password, security_id securityId, " +
                "account_id accountId, from_recipient fromRecipient, status FROM smtp_details " +
                "WHERE account_id = " + accountId;
        try {
            log.debug("getting SMTP details.");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(SMTPDetailsBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching SMTP details from 'smtp_details' table. Details: {}", e.getMessage());
        }
        return null;
    }

    public void updateSMTPDetails(SMTPDetailsBean smtpDetailsBean) throws ControlCenterException {
        String query = "UPDATE smtp_details SET address = ?, port = ?, username = ?, password = ?, " +
                "security_id = ?, from_recipient = ?, user_details_id = ?, updated_time = ? " +
                "WHERE account_id = " + smtpDetailsBean.getAccountId();
        try {
            log.debug("updating SMTP configurations.");
            jdbcTemplate.update(query, smtpDetailsBean.getAddress(), smtpDetailsBean.getPort(), smtpDetailsBean.getUsername(), smtpDetailsBean.getPassword(),
                    smtpDetailsBean.getSecurityId(), smtpDetailsBean.getFromRecipient(), smtpDetailsBean.getLastModifiedBy(), smtpDetailsBean.getUpdatedTime());
        } catch (Exception e) {
            log.error("Exception encountered while updating SMTP details in 'smtp_details' table. Details: {}", e.getMessage());
            throw new ControlCenterException("Error occurred while updating SMTP details for account.");
        }
    }
}

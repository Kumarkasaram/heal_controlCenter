package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.NotificationSettingsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.dao.mysql.NotificationsDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.UserAccountPojo;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GetNotificationSettingsBL implements BusinessLogic<Object, UserAccountPojo, List<NotificationSettingsBean>> {

    @Autowired
    NotificationsDao notificationsDao;
    @Autowired
    AccountsDao accountDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    DateTimeUtil dateTimeUtil;

    @Value("${openForLong.minDuration.time.min:15}")
    private int MIN_OPEN_FOR_LONG;
    @Value("${openForTooLong.minDuration.time.min:30}")
    private int MIN_OPEN_FOR_TOO_LONG;
    @Value("${openForLong.maxDuration.time.min:1440}")
    private int MAX_OPEN_FOR_LONG;
    @Value("${openForTooLong.maxDuration.time.min:2880}")
    private int MAX_OPEN_FOR_TOO_LONG;

    @Override
    public UtilityBean<Object> clientValidation(Object requestBody, String... requestParams) throws ClientException {
        String userId;

        if (requestParams[0].isEmpty()) {
            log.error(UIMessages.AUTH_KEY_EMPTY);
            throw new ClientException(UIMessages.AUTH_KEY_EMPTY);
        }

        if (requestParams[1].isEmpty()) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
            throw new ClientException(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
        }

        try {
            userId = commonUtils.getUserId(requestParams[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while getting UserId. Details: ", e);
            throw new ClientException("Error occurred while fetching UserId.");
        }

        return UtilityBean.builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .build();
    }

    @Override
    public UserAccountPojo serverValidation(UtilityBean<Object> utilityBean) throws ServerException {

        AccountBean account = accountDao.getAccountByIdentifier(utilityBean.getAccountIdentifier());
        UserAccountPojo user = new UserAccountPojo();
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        user.setUserId(utilityBean.getUserId());
        user.setAccount(account);

        return user;
    }

    @Override
    public List<NotificationSettingsBean> process(UserAccountPojo user) throws DataProcessingException {
        List<NotificationSettingsBean> settingsList = new ArrayList<>();
        List<NotificationSettingsBean> notificationSettings = notificationsDao.getNotificationSetting(user.getAccount().getId());

        if (notificationSettings.isEmpty()) {
            log.info("Notification Settings not found for the account {}. Setting Default Values.", user.getAccount().getIdentifier());
            int[] ids;
            try {
                ids = addDefaultNotificationSettings(user.getAccount().getId(), user.getUserId());
            } catch (ControlCenterException e) {
                throw new DataProcessingException(e.getMessage());
            }
            if (ids.length > 0) {
                log.info("Successfully added default Notification Settings to the account {}.", user.getAccount().getIdentifier());
                notificationSettings = notificationsDao.getNotificationSetting(user.getAccount().getId());
            } else {
                log.error("Error adding default Notification Settings to the account {}.", user.getAccount().getIdentifier());
                throw new DataProcessingException("Error adding default Notification Settings to the account. Internal server error. Please contact the administrator.");
            }
        }

        for (NotificationSettingsBean setting : notificationSettings) {
            String typeName = masterDataDao.getMstSubTypeBySubTypeId(setting.getTypeId()).getSubTypeName();
            setting.setTypeName(typeName);
            if (Constants.LONG.equalsIgnoreCase(typeName)) {
                setting.getProperties().put("min", MIN_OPEN_FOR_LONG);
                setting.getProperties().put("max", MAX_OPEN_FOR_LONG);
            } else if (Constants.TOO_LONG.equalsIgnoreCase(typeName)) {
                setting.getProperties().put("min", MIN_OPEN_FOR_TOO_LONG);
                setting.getProperties().put("max", MAX_OPEN_FOR_TOO_LONG);
            }
            settingsList.add(setting);
        }
        return settingsList;
    }

    public int[] addDefaultNotificationSettings(int accountId, String userId) throws ControlCenterException {
        log.trace("Method Invoked : GetNotificationSettingsBL/addDefaultNotificationSettings");
        int longId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.LONG).getSubTypeId();
        int tooLongId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.TOO_LONG).getSubTypeId();
        int[] ids = {};

        try {
            List<NotificationSettingsBean> defaultSettingsList = new ArrayList<>();
            NotificationSettingsBean settingsLong = new NotificationSettingsBean();
            NotificationSettingsBean settingsTooLong = new NotificationSettingsBean();
            Timestamp timestamp = new Timestamp(dateTimeUtil.getDateInGMT(System.currentTimeMillis()).getTime());
            List<NotificationSettingsBean> settingsDB = notificationsDao.getNotificationSetting(1);

            for (NotificationSettingsBean settings : settingsDB) {
                if (settings.getTypeId() == longId) {
                    settingsLong.setDurationInMin(settings.getDurationInMin());
                }
                if (settings.getTypeId() == tooLongId) {
                    settingsTooLong.setDurationInMin(settings.getDurationInMin());
                }
            }
            settingsLong.setUpdatedTime(timestamp.toString());
            settingsLong.setCreatedTime(timestamp.toString());
            settingsLong.setLastModifiedBy(userId);
            settingsLong.setAccountId(accountId);
            settingsLong.setTypeId(longId);

            settingsTooLong.setUpdatedTime(timestamp.toString());
            settingsTooLong.setCreatedTime(timestamp.toString());
            settingsTooLong.setLastModifiedBy(userId);
            settingsTooLong.setAccountId(accountId);
            settingsTooLong.setTypeId(tooLongId);

            defaultSettingsList.add(settingsLong);
            defaultSettingsList.add(settingsTooLong);

            ids = notificationsDao.addNotificationSettings(defaultSettingsList);
            return ids;
        } catch (Exception e) {
            log.error("Error occurred while adding default notification Settings. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
        }
        return ids;
    }
}

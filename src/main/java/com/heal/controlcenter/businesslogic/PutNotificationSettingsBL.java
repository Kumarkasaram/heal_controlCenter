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
import com.heal.controlcenter.pojo.NotificationSettingsPojo;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class PutNotificationSettingsBL implements BusinessLogic<List<NotificationSettingsPojo>, UtilityBean<List<NotificationSettingsPojo>>, Object> {

    @Autowired
    NotificationsDao notificationsDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    AccountsDao accountDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    DateTimeUtil dateTimeUtil;

    private int longId;
    private int tooLongId;
    private String longName;
    private String tooLongName;

    @Value("${openForLong.minDuration.time.min:15}")
    private int MIN_OPEN_FOR_LONG;
    @Value("${openForTooLong.minDuration.time.min:30}")
    private int MIN_OPEN_FOR_TOO_LONG;
    @Value("${openForLong.maxDuration.time.min:1440}")
    private int MAX_OPEN_FOR_LONG;
    @Value("${openForTooLong.maxDuration.time.min:2880}")
    private int MAX_OPEN_FOR_TOO_LONG;

    @Override
    public UtilityBean<List<NotificationSettingsPojo>> clientValidation(List<NotificationSettingsPojo> settings, String... requestParams) throws ClientException {
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
            log.error("Exception encountered while getting UserId. Details: {}", e.getMessage());
            throw new ClientException("Error occurred while fetching UserId.");
        }

        if (settings.isEmpty()) {
            log.error(UIMessages.REQUEST_BODY_NULL);
            throw new ClientException(UIMessages.REQUEST_BODY_NULL);
        }

        Set<NotificationSettingsPojo> settingsSet = new HashSet<>(settings);
        if (settingsSet.size() < settings.size()) {
            log.error(UIMessages.DUPLICATE_NOTIFICATION_SETTING);
            throw new ClientException(UIMessages.DUPLICATE_NOTIFICATION_SETTING);
        }

        for (NotificationSettingsPojo setting : settings) {
            setting.validate();
            if (!setting.getError().isEmpty()) {
                String err = setting.getError().toString();
                log.error(err);
                throw new ClientException(err);
            }
        }
        try {
            initialiseVariables();
        } catch (ControlCenterException e) {
            e.printStackTrace();
        }
        validateData(settings);

        return UtilityBean.<List<NotificationSettingsPojo>>builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .pojoObject(settings)
                .build();
    }

    @Override
    public UtilityBean<List<NotificationSettingsPojo>> serverValidation(UtilityBean<List<NotificationSettingsPojo>> utilityBean) throws ServerException {
        AccountBean account = accountDao.getAccountByIdentifier(utilityBean.getAccountIdentifier());
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        utilityBean.setAccount(account);

        return utilityBean;
    }

    @Override
    public Object process(UtilityBean<List<NotificationSettingsPojo>> settingsBean) throws DataProcessingException {
        List<NotificationSettingsPojo> notificationSettings = settingsBean.getPojoObject();
        List<NotificationSettingsBean> settings = new ArrayList<>();
        Timestamp timestamp = null;

        try {
            timestamp = new Timestamp(dateTimeUtil.getDateInGMT(System.currentTimeMillis()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<NotificationSettingsBean> settingsDB = notificationsDao.getNotificationSetting(settingsBean.getAccount().getId());
        if(settingsDB.isEmpty()) {
            log.error("Notification Settings not found for account {}.", settingsBean.getAccount().getIdentifier());
            throw new DataProcessingException("Notification Settings not found for the account.");
        }

        int openForLong = notificationSettings.stream().filter(notificationSetting -> notificationSetting.getTypeName().equals(longName))
                .filter(notificationSetting -> (notificationSetting.getTypeId() == longId)).map(NotificationSettingsPojo::getDurationInMin).map(Float::intValue).findAny().orElse(MIN_OPEN_FOR_LONG);
        int openForTooLong = notificationSettings.stream().filter(notificationSetting -> notificationSetting.getTypeName().equals(tooLongName))
                .filter(notificationSetting -> (notificationSetting.getTypeId() == tooLongId)).map(NotificationSettingsPojo::getDurationInMin).map(Float::intValue).findAny().orElse(MIN_OPEN_FOR_TOO_LONG);

        if (openForLong < MIN_OPEN_FOR_LONG) {
            openForLong = MIN_OPEN_FOR_LONG;
        } else if (openForLong > MAX_OPEN_FOR_LONG) {
            openForLong = MAX_OPEN_FOR_LONG;
        }

        if (openForTooLong < MIN_OPEN_FOR_TOO_LONG) {
            openForTooLong = MIN_OPEN_FOR_TOO_LONG;
        } else if (openForTooLong > MAX_OPEN_FOR_TOO_LONG) {
            openForTooLong = MAX_OPEN_FOR_TOO_LONG;
        }

        int remainder = (openForTooLong % openForLong);
        if (remainder != 0) {
            openForTooLong = ((openForTooLong / openForLong) * openForLong);
        }
        if (openForLong == openForTooLong) {
            openForTooLong = 2 * openForLong;
        }

        for (NotificationSettingsPojo setting : notificationSettings) {
            NotificationSettingsBean entity = new NotificationSettingsBean();
            entity.setAccountId(settingsBean.getAccount().getId());
            entity.setUpdatedTime(String.valueOf(timestamp));
            if((setting.getTypeName().equals(longName) && (setting.getTypeId() == longId))) {
                entity.setDurationInMin(openForLong);
            }
            else if((setting.getTypeName().equals(tooLongName) && (setting.getTypeId() == tooLongId))) {
                entity.setDurationInMin(openForTooLong);
            }
            entity.setTypeId(setting.getTypeId());
            entity.setLastModifiedBy(settingsBean.getUserId());
            settings.add(entity);
        }

        try {
            notificationsDao.updateNotificationSetting(settings);
        } catch (Exception e) {
            throw new DataProcessingException("Error occurred, couldn't update notification settings.");
        }

        return null;
    }

    public void validateData(List<NotificationSettingsPojo> notificationSettings) throws ClientException {
        Map<String, String> error = new HashMap<>();

        for (NotificationSettingsPojo settings : notificationSettings) {
            if (!((settings.getTypeName().equals(longName)) || (settings.getTypeName().equals(tooLongName)))) {
                log.error(UIMessages.INVALID_TYPE_NAME);
                error.put("Type name", UIMessages.INVALID_TYPE_NAME);
            }

            if (!((settings.getTypeName().equals(longName) && (settings.getTypeId() == longId)) || (settings.getTypeName().equals(tooLongName) && (settings.getTypeId() == tooLongId)))) {
                log.error(UIMessages.INVALID_COMBINATION);
                error.put("Type name/id combination", UIMessages.INVALID_COMBINATION);
            }

            if ((settings.getDurationInMin() <= 0)) {
                log.error(UIMessages.INVALID_DURATION);
                error.put("Duration In Minutes", UIMessages.INVALID_DURATION);
            }

            if (!error.isEmpty()) {
                String err = error.toString();
                log.error(err);
                throw new ClientException(err);
            }
        }
    }

    public void initialiseVariables() throws ControlCenterException {
        longId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.LONG).getSubTypeId();
        tooLongId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.TOO_LONG).getSubTypeId();
        longName = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.LONG).getSubTypeName();
        tooLongName = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.TOO_LONG).getSubTypeName();
    }
}

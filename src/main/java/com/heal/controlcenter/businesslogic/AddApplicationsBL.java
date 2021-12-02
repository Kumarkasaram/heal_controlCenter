package com.heal.controlcenter.businesslogic;

import com.appnomic.appsone.util.ConfProperties;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.Application;
import com.heal.controlcenter.pojo.IdPojo;
import com.heal.controlcenter.pojo.TagMappingDetails;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UserValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddApplicationsBL {

    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ControllerDao controllerDao;
    @Autowired
    private AccountsDao accountsDao;
    @Autowired
    private MasterDataDao masterDataDao;
    @Autowired
    private ApplicationNotifAndPercentileDao applicationNotifAndPercentileDao;
    @Autowired
    private TagsDao tagsDao;
    @Autowired
    private UserValidationUtil userValidationUtil;
    @Autowired
    private DateTimeUtil dateTimeUtil;

    public UtilityBean<Application> clientValidation(Application application, String... params) throws ClientException {
        if (null == params) {
            log.error(UIMessages.REQUEST_NULL);
            throw new ClientException(UIMessages.REQUEST_NULL);
        }

        String authKey = params[0];
        if (authKey == null || authKey.trim().isEmpty()) {
            log.error("Invalid authorization token. Reason: It is either NULL or empty.");
            throw new ClientException("Invalid authorization token");
        }

        String user;
        try {
            user = commonUtils.getUserId(authKey);
        } catch (Exception e) {
            log.error("Unable to fetch userId from authorization token");
            throw new ClientException("Unable to fetch userId from authorization token");
        }

        application.setUserId(user);
        application.setAccountIdentifier(params[1]);

        return UtilityBean.<Application>builder()
                .pojoObject(application)
                .build();
    }

    public ApplicationBean serverValidation(UtilityBean<Application> utilityBean) throws ServerException {
        Application app = utilityBean.getPojoObject();

        UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(app.getUserId(), app.getAccountIdentifier());
        if (userAccessDetails == null) {
            log.error("User access details unavailable for user [{}]", app.getUserId());
            throw new ServerException("User access details unavailable");
        }

        UserInfoBean userInfoBean;
        try {
            userInfoBean = userDao.getUserDetails(app.getUserId());
        } catch (ControlCenterException e) {
            throw new ServerException(e.getMessage());
        }

        UserProfileBean userProfileBean;
        try {
            userProfileBean = userDao.getUserProfile(userInfoBean.getProfileId());
        } catch (ControlCenterException e) {
            throw new ServerException(e.getMessage());
        }

        if (!"Super Admin".equalsIgnoreCase(userProfileBean.getUserProfileName()) || !"Heal Admin".equalsIgnoreCase(userProfileBean.getUserProfileName())) {
            throw new ServerException("User is not allowed to create an application. " +
                    "Only 'Super Admin' and 'Heal Admin' can create applications. " + app.getUserId());
        }

        AccountBean account = accountsDao.getAccountDetailsForIdentifier(app.getAccountIdentifier());

        if (account == null) {
            log.error("Invalid account identifier [{}]", app.getAccountIdentifier());
            throw new ServerException("Invalid Account Identifier");
        }

        int accountId = account.getId();

        if (app.getIdentifier() == null || app.getIdentifier().trim().length() == 0) {
            app.setIdentifier(UUID.randomUUID().toString());
        } else {
            ControllerBean existingAppWithIdentifier;
            try {
                existingAppWithIdentifier = controllerDao.getApplicationIdByIdentifier(app.getIdentifier());
            } catch (ControlCenterException e) {
                throw new ServerException("Unable to validate application identifier");
            }
            if (existingAppWithIdentifier != null) {
                log.error("Application with identifier [{}] already exists", app.getIdentifier());
                throw new ServerException("Application with identifier already exists");
            }
        }

        ControllerBean existingAppWithName;
        try {
            existingAppWithName = controllerDao.getApplicationIdByName(app.getName());
        } catch (ControlCenterException e) {
            throw new ServerException("Unable to validate application name");
        }
        if (existingAppWithName != null) {
            log.error("Application with name [{}] already exists", app.getName());
            throw new ServerException("Application with name already exists");
        }

        TimezoneBean timezone;
        try {
            timezone = masterDataDao.getTimeZoneWithId(app.getTimezone());
        } catch (ControlCenterException e) {
            log.error("Invalid timezone [{}]", app.getTimezone());
            throw new ServerException(e.getMessage());
        }

        return ApplicationBean.builder()
                .name(app.getName().trim())
                .identifier(app.getIdentifier())
                .timezone(timezone)
                .accountId(accountId)
                .userId(app.getUserId())
                .build();
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public IdPojo process(ApplicationBean bean) throws DataProcessingException {

        ControllerBean controllerBean = new ControllerBean();
        controllerBean.setName(bean.getName());
        controllerBean.setIdentifier(bean.getIdentifier());
        controllerBean.setAccountId(bean.getAccountId());
        controllerBean.setLastModifiedBy(bean.getUserId());
        controllerBean.setCreatedTime(dateTimeUtil.getCurrentTimestampInGMT().toString());
        controllerBean.setUpdatedTime(dateTimeUtil.getCurrentTimestampInGMT().toString());
        controllerBean.setControllerTypeId(191);

        int appId;
        try {
            appId = controllerDao.addController(controllerBean);
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }

        try {
            TagMappingDetails timeZoneTag = TagMappingDetails.builder()
                    .tagId(tagsDao.getTagDetails(Constants.TIME_ZONE_TAG).getId())
                    .tagKey(bean.getTimezone().getTimeZoneId())
                    .tagValue(String.valueOf(bean.getTimezone().getOffset()))
                    .objectId(appId)
                    .objectRefTable(Constants.CONTROLLER)
                    .accountId(bean.getAccountId())
                    .createdTime(controllerBean.getCreatedTime())
                    .updatedTime(controllerBean.getUpdatedTime())
                    .build();
            int mappingId = tagsDao.addTagMappingDetails(timeZoneTag);
            if(mappingId <= 0) {
                log.error("Timezone mapping to application [{}] failed", bean.getIdentifier());
                throw new DataProcessingException("Timezone mapping to application failed");
            }
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }

        try {
            List<DefaultNotificationPreferences> list = generateDefaultNotificationList(appId, bean.getAccountId(), bean.getUserId());
            applicationNotifAndPercentileDao.addDefaultNotificationPreferences(list);
        } catch (Exception e) {
            log.error("Error while adding default notification preferences for application name [{}], identifier [{}] for accountId [{}]. Details: ",
                    bean.getName(), bean.getIdentifier(), bean.getAccountId(), e);
            throw new DataProcessingException(e.getMessage());
        }

        List<ApplicationPercentilesBean> beanList = createApplicationPercentiles(bean.getAccountId(), appId, bean.getUserId());
        try {
            applicationNotifAndPercentileDao.addApplicationPercentiles(beanList);
        } catch (ControlCenterException e) {
            log.error("Error while adding application percentiles for application name [{}], identifier [{}] for accountId [{}]. Details: ",
                    bean.getName(), bean.getIdentifier(), bean.getAccountId(), e);
            throw new DataProcessingException(e.getMessage());
        }

        return IdPojo.builder()
                .id(appId)
                .name(bean.getName())
                .identifier(bean.getIdentifier())
                .build();
    }

    public List<DefaultNotificationPreferences> generateDefaultNotificationList(int id, int accountId, String userId) throws ControlCenterException {
        List<SignalTypeSeverityMapping> preferencesType = getDefaultPreferencesType();

        int defaultNotificationTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.NOTIFICATION_TYPE_LITERAL, Constants.IMMEDIATELY).getSubTypeId();

        return preferencesType.parallelStream().map(m -> {
            DefaultNotificationPreferences defaultNotificationPreferences = new DefaultNotificationPreferences();
            defaultNotificationPreferences.setApplicationId(id);

            defaultNotificationPreferences.setNotificationTypeId(defaultNotificationTypeId);
            defaultNotificationPreferences.setSignalTypeId(m.getSignalTypeId());
            defaultNotificationPreferences.setSignalSeverityId(m.getSignalSeverityId());
            defaultNotificationPreferences.setAccountId(accountId);
            defaultNotificationPreferences.setCreatedTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()));
            defaultNotificationPreferences.setUpdatedTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()));
            defaultNotificationPreferences.setUserDetailsId(userId);

            return defaultNotificationPreferences;
        }).collect(Collectors.toList());
    }

    private List<SignalTypeSeverityMapping> getDefaultPreferencesType() throws ControlCenterException {
        List<SignalTypeSeverityMapping> signalTypeSeverityMappingList = new ArrayList<>();

        int problemTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_TYPE_LITERAL, Constants.PROBLEM).getSubTypeId();
        int earlyWarningTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_TYPE_LITERAL, Constants.EARLY_WARNING).getSubTypeId();
        int infoTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_TYPE_LITERAL, Constants.INFO).getSubTypeId();
        int batchTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_TYPE_LITERAL, Constants.BATCH).getSubTypeId();

        int severeTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_SEVERITY_TYPE_LITERAL, Constants.SEVERE).getSubTypeId();
        int defaultTypeId = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SIGNAL_SEVERITY_TYPE_LITERAL, Constants.DEFAULT).getSubTypeId();

        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(problemTypeId, severeTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(problemTypeId, defaultTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(earlyWarningTypeId, severeTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(earlyWarningTypeId, defaultTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(infoTypeId, severeTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(infoTypeId, defaultTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(batchTypeId, severeTypeId));
        signalTypeSeverityMappingList.add(new SignalTypeSeverityMapping(batchTypeId, defaultTypeId));

        return signalTypeSeverityMappingList;
    }

    public List<ApplicationPercentilesBean> createApplicationPercentiles(int accountId, int applicationId, String userId) {
        String defaultPercentiles = ConfProperties.getString(Constants.APPLICATION_PERCENTILES_DEFAULT_CONFIGURATION, Constants.APPLICATION_PERCENTILES_DEFAULT_VALUES);
        String[] percentile = defaultPercentiles.split(",");

        return Arrays.stream(percentile)
                .map(i -> ApplicationPercentilesBean.builder()
                        .applicationId(applicationId)
                        .accountId(accountId)
                        .displayName(i)
                        .percentileValue(Integer.parseInt(i))
                        .userDetailsId(userId)
                        .createdTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                        .updatedTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                        .build())
                .collect(Collectors.toList());
    }
}

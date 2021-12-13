package com.heal.controlcenter.businesslogic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GetAuditTrailBL {
    @Autowired
    AccountsDao accountsDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    ValidationsUtils validationsUtils;
    @Autowired
    UserValidationUtil userValidationUtil;
    @Autowired
    ControllerDao controllerDao;
    @Autowired
    TimeZoneDao timeZoneDao;
    @Autowired
    AuditTrailDao auditTrailDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    UserDao userDao;
    @Autowired
    DateTimeUtil dateTimeUtil;

    private static final ObjectMapper objectMapper = CommonUtils.getObjectMapperWithHtmlEncoder();
    private final Map<Integer, MasterBigFeatureBean> activityTypeMap = masterDataDao.getBigFeaturesMasterData().stream().collect(Collectors.toMap(MasterBigFeatureBean::getId, e -> e));
    private final Map<Integer, MasterPageActionBean> subActivityTypeMap = masterDataDao.getPageActionsMasterData().stream().collect(Collectors.toMap(MasterPageActionBean::getId, e -> e));
    private Map<Integer, List<ViewApplicationServiceMappingBean>> svcMappedToApp = new HashMap<>();

    public UtilityBean<AuditTrailBean> clientValidation(Map request, String... requestObject ) throws ClientException {
        UtilityBean<AuditTrailBean> utilityBean = new UtilityBean();
        if (requestObject == null) {
            log.error(UIMessages.REQUEST_NULL);
            throw new ClientException(UIMessages.REQUEST_NULL);
        }
        String authKey = requestObject[0];
        if (authKey == null || authKey.trim().isEmpty()) {
            log.error("Invalid authorization token. Reason: It is either NULL or empty");
            throw new ClientException("Invalid authorization token");
        }

        String identifier = requestObject[1];
        if (identifier == null || identifier.trim().isEmpty()) {
            log.error("Invalid account identifier. Reason: It is either NULL or empty");
            throw new ClientException("Invalid account identifier");
        }

        String[] fromTime = (String[]) request.get(Constants.REQUEST_PARAM_FROM_TIME);
        if (fromTime == null || StringUtils.isEmpty(fromTime[0])) {
            log.error("invalid from time");
            throw new ClientException("invalid fromTime");
        }

        String[] toTime = (String[]) request.get(Constants.REQUEST_PARAM_TO_TIME);
        if (toTime == null || StringUtils.isEmpty(toTime[0])) {
            log.error("invalid toTime");
            throw new ClientException("invalid totime");
        }

        String[] serviceIds = (String[]) request.get(Constants.AUDIT_PARAM_SERVICE_NAME);
        String[] applicationIds = (String[]) request.get(Constants.AUDIT_PARAM_APPLICATION_NAME);
        String[] activityTypeIds = (String[]) request.get(Constants.AUDIT_PARAM_ACTIVITY_TYPE);
        String[] userId = (String[]) request.get(Constants.AUDIT_PARAM_USER_NAME);

        List<Integer> appIds = getIds(applicationIds);
        List<Integer> srvIds = getIds(serviceIds);
        List<Integer> activityIds = getActivityTypeIds(activityTypeIds);

        AuditTrailBean auditTrailBean = null;

        if (!StringUtils.isEmpty(toTime[0]) && !StringUtils.isEmpty(fromTime[0])) {
            auditTrailBean = AuditTrailBean.builder()
                    .appIds(appIds)
                    .serviceIds(srvIds)
                    .bigFeatureIds(activityIds).build();
            auditTrailBean.setFromTime(parseFromTime(fromTime[0]));
            auditTrailBean.setToTime(parseToTime(toTime[0]));
            if (userId != null && !StringUtils.isEmpty(userId[0])) {
                auditTrailBean.setUserId(userId[0]);
            }
        }
        return UtilityBean.<AuditTrailBean>builder()
                .accountIdentifier(identifier)
                .authToken(authKey)
                .pojoObject(auditTrailBean)
                .build();
    }


    public AuditTrailBean serverValidation(UtilityBean<AuditTrailBean> utilityBean) throws ServerException {
        try {
            String userId = commonUtils.getUserId(utilityBean.getAuthToken());
            if (userId == null) {
                log.error(UIMessages.AUTH_KEY_INVALID);
                throw new ServerException(UIMessages.AUTH_KEY_INVALID);
            }
            AccountBean accounts = validationsUtils.validAndGetAccount(utilityBean.getAccountIdentifier());
            if (accounts == null) {
                log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
                throw new ControlCenterException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            }
            int accountId = accounts.getId();

            List<Integer> accessibleApplicationsForUser = userValidationUtil.getAccessibleApplicationsForUser(userId, accounts.getIdentifier())
                    .parallelStream()
                    .map(app -> Integer.parseInt(app.getAppId()))
                    .collect(Collectors.toList());
            List<ViewApplicationServiceMappingBean> mappedToApplication = controllerDao.getServicesMappedToApplicationByApplicationId(accountId, accessibleApplicationsForUser);
            svcMappedToApp = mappedToApplication
                    .parallelStream()
                    .collect(Collectors.groupingBy(ViewApplicationServiceMappingBean::getServiceId));
            List<Integer> servicesMappedToApplication = new ArrayList<>(svcMappedToApp.keySet());
            MasterTimezoneBean timezoneBean;
            timezoneBean = timeZoneDao.getTimezoneByAccountId(accounts.getId());
            if (timezoneBean == null) {
                log.error(UIMessages.INVALID_TIMEZONE);
                throw new ServerException(UIMessages.INVALID_TIMEZONE);
            }

            long offset = timezoneBean.getTimeOffset();
            String gmtTZ = String.format(Constants.TIME_ZONE_FORMAT,
                    offset < Constants.DEFAULT_VALUE1 ? "-" : "+",
                    Math.abs(offset) / Constants.DEFAULT_VALUE2,
                    Math.abs(offset) / Constants.DEFAULT_VALUE3 % Constants.DEFAULT_VALUE4);
            AuditTrailBean auditTrailBean = (AuditTrailBean) utilityBean.getPojoObject();
            auditTrailBean.setAccountId(accountId);
            auditTrailBean.setTimeZone(gmtTZ);
            auditTrailBean.setDefaultTimeZone(Constants.DEFAULT_TIME_ZONE);
            for (Integer appId : utilityBean.getPojoObject().getAppIds()) {
                if (!accessibleApplicationsForUser.contains(appId)) {
                    throw new ServerException(UIMessages.ERROR_INVALID_APPLICATION_ID);
                }
            }
            for (Integer svcId : utilityBean.getPojoObject().getServiceIds()) {
                if (!servicesMappedToApplication.contains(svcId)) {
                    throw new ServerException(UIMessages.ERROR_INVALID_SERVICE_ID);
                }
            }
            for (Integer featureId : utilityBean.getPojoObject().getBigFeatureIds()) {
                if (!activityTypeMap.containsKey(featureId)) {
                }
            }
            if (utilityBean.getPojoObject().getServiceIds() == null || utilityBean.getPojoObject().getServiceIds().size() == 0) {
                utilityBean.getPojoObject().setServiceIds(servicesMappedToApplication);
            }
            return auditTrailBean;
        } catch (Exception ex) {
            throw new ServerException("Exception occur at GetAuditTrailBL :"+ ex.getMessage());
        }
    }


    public List<AuditTrailPojo> process(AuditTrailBean bean) throws DataProcessingException {
        String whereClause = getWhereClause(bean);
        List<AuditTrailPojo> auditTrailData;
        try {
            List<AuditBean> auditBeanDb =auditTrailDao.getAuditTrail(bean, whereClause);
            auditTrailData = getAuditDataList(bean, auditBeanDb);
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }
        auditTrailData.sort(Comparator.comparing(AuditTrailPojo::getAuditTime).reversed().thenComparing(AuditTrailPojo::getUpdatedBy));

        return auditTrailData;
    }

    private List<Integer> getIds(String[] ids) throws ClientException {
        List<Integer> appIds = new ArrayList<>();
        if (ids != null && ids.length > 0 && ids[0].length() > 0) {
            String[] idList = ids[0].split(",");
            for (String id : idList) {
                if (StringUtils.isNumber(id)) {
                    appIds.add(Integer.parseInt(id));
                } else {
                    log.error(UIMessages.ERROR_INVALID_APPLICATION_ID);
                    throw new ClientException(UIMessages.ERROR_INVALID_APPLICATION_ID);
                }
            }
        }
        return appIds;
    }

    private List<Integer> getActivityTypeIds(String[] activityIds) throws ClientException {
        List<Integer> activityTypeIds = new ArrayList<>();
        if (activityIds != null && activityIds.length > 0 && activityIds[0].length() > 0) {
            String[] activityIdList = activityIds[0].split(",");
            for (String id : activityIdList) {
                if (StringUtils.isNumber(id)) {
                    activityTypeIds.add(Integer.parseInt(id));
                } else {
                    log.error(UIMessages.ERROR_INVALID_BIG_FEATURE_ID);
                    throw new ClientException(UIMessages.ERROR_INVALID_BIG_FEATURE_ID);
                }
            }
        }
        return activityTypeIds;
    }


    private long parseFromTime(String fromTime) throws ClientException {
        String fTime;
        if (StringUtils.getLong(fromTime) != 0) {
            if (fromTime.length() > 10) {
                fTime = fromTime.substring(0, 10);
            } else {
                fTime = fromTime;
            }
        } else {
            log.error(Constants.INVALID_FROM_TIME);
            throw new ClientException(Constants.INVALID_FROM_TIME);
        }
        return Long.parseLong(fTime);
    }

    private long parseToTime(String toTime) throws ClientException {
        String tTime;
        if (StringUtils.getLong(toTime) != 0) {
            if (toTime.length() > 10) {
                tTime = toTime.substring(0, 10);
            } else {
                tTime = toTime;
            }
        } else {
             log.error(Constants.INVALID_TO_TIME);
            throw new ClientException(Constants.INVALID_TO_TIME);
        }
        return Long.parseLong(tTime);
    }

    private String getWhereClause(AuditTrailBean bean) {
        StringBuilder whereClause = new StringBuilder("account_id in (0,1, " + bean.getAccountId() + Constants.DB_CONDITION);
        if (!bean.getBigFeatureIds().isEmpty()) {
            whereClause.append(" mst_big_feature_id in (");
            for (Integer id : bean.getBigFeatureIds()) {
                whereClause.append(id).append(",");
            }
            whereClause.deleteCharAt(whereClause.length()-1);
            whereClause.append(") and ");
        }
        if (!bean.getServiceIds().isEmpty()) {
            whereClause.append(" service_id in (");
            for (Integer id : bean.getServiceIds()) {
                whereClause.append(id).append(",");
            }
            whereClause.append(" 0) and ");
        }
        if (bean.getUserId() != null) {
            whereClause.append(" audit_user = '").append(bean.getUserId()).append("' and ");
        }
        return whereClause.toString();
    }


    private List<AuditTrailPojo> getAuditDataList(AuditTrailBean bean, List<AuditBean> auditBeanDb) throws ControlCenterException {
        Map<Integer, ControllerBean> ControllerMap = controllerDao.getControllerList(bean.getAccountId())
                .stream()
                .collect(Collectors.toMap(e -> Integer.parseInt(e.getAppId()), e -> e));

        List<AuditTrailPojo> auditTrailData = new ArrayList<>();
        for (AuditBean auditBean : auditBeanDb) {
            AuditTrailPojo auditTrailPojo = new AuditTrailPojo();

            if (auditBean.getBigFeatureId() > 0) {
                MasterBigFeatureBean activityType = activityTypeMap.get(auditBean.getBigFeatureId());
                auditTrailPojo.setActivityType(activityType.getName());
            }
            if (auditBean.getPageActionId() > 0) {
                MasterPageActionBean subActivityType = subActivityTypeMap.get(auditBean.getPageActionId());
                auditTrailPojo.setSubActivityType(subActivityType.getName());
            }
            if (auditBean.getSvcId() > 0) {
                ControllerBean service = ControllerMap.get(auditBean.getSvcId());
                if (service != null) {
                    auditTrailPojo.setServiceName(service.getName());
                }
            }

            try {
                Date date = dateTimeUtil.getDateInGMT(Long.parseLong(auditBean.getAuditTime()));
                Long auditEpochTime = dateTimeUtil.getGMTToEpochTime(String.valueOf(new Timestamp(date.getTime())));
                auditTrailPojo.setAuditTime(auditEpochTime);
            } catch (ParseException e) {
                log.info("Error parsing audit timestamp. Details: {}", e.getMessage(), e);
                throw new ControlCenterException("Error while parsing audit timestamp");
            }

            auditTrailPojo.setOperationType(auditBean.getOperationType());

            setData(auditTrailPojo, auditBean);

            auditTrailData.addAll(setApplicationNames(auditTrailPojo,auditBean));
        }

        return auditTrailData;
    }

    private void setData(AuditTrailPojo auditTrailPojo, AuditBean auditBean) throws ControlCenterException {
        Map<String, UserDetailsBean> allUserMap = null;
        try {
            allUserMap = userDao.getUsers().stream().collect(Collectors.toMap(UserDetailsBean::getId, e -> e));
        } catch (ControlCenterException e) {
            throw new ControlCenterException("Error while fetching userDetail");
        }
        if (auditBean.getUpdatedBy() != null) {
            UserDetailsBean userAttributesBean = allUserMap.get(auditBean.getUpdatedBy());
            if (userAttributesBean != null) {
                auditTrailPojo.setUpdatedBy(userAttributesBean.getUserName());
            } else {
                auditTrailPojo.setUpdatedBy(auditBean.getUpdatedBy());
            }
        }
        String auditData = auditBean.getAuditData();

        Map<String, Map<String, String>> valueForUpdate;

        if (auditData != null && auditBean.getOperationType() != null) {
            try {
                valueForUpdate = objectMapper.readValue(auditData,
                        new TypeReference<Map<String, Map<String, String>>>() {
                        });
                auditTrailPojo.setValue(valueForUpdate);
            } catch (IOException e) {
                log.info(UIMessages.ERROR_PARSING_AUDIT_VALUE + " : {}", e.getMessage());
            }
        }
    }

    private List<AuditTrailPojo> setApplicationNames(AuditTrailPojo auditTrailPojo, AuditBean auditBean) {
        List<AuditTrailPojo> result = new ArrayList<>();
        List<ViewApplicationServiceMappingBean> viewApplicationServiceMappingBeans = svcMappedToApp.get(auditBean.getSvcId());
        if(viewApplicationServiceMappingBeans != null && !viewApplicationServiceMappingBeans.isEmpty()){
            for(ViewApplicationServiceMappingBean app: viewApplicationServiceMappingBeans){
                //AuditTrailPojo pojo = new AuditTrailPojo(auditTrailPojo);
                auditTrailPojo.setApplicationName(app.getApplicationName());
                result.add(auditTrailPojo);
            }

        }else{
            result.add(auditTrailPojo);
        }
        return result;
    }

}

package com.heal.controlcenter.businesslogic;

import com.google.common.base.Throwables;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.cassandra.ConfigureKpiCassandraDao;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.TagMappingDetails;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UserValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Comparator.nullsFirst;

@Component
@Slf4j
public class GetHealthOfInstancesBL {
    @Autowired
    AccountsDao accountsDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    ComponentDao componentDao;
    @Autowired
    UserValidationUtil userValidationUtil;
    @Autowired
    ControllerDao controllerDao;
    @Autowired
    TagsDao tagsDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    ConfigureKpiCassandraDao configureKpiCassandraDao;

    public UtilityBean<Object> clientValidation(Object requestBody, String... requestObject ) throws ClientException {
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
        return UtilityBean.builder()
                .accountIdentifier(identifier)
                .authToken(authKey)
                .build();
    }

    public UtilityBean<Object> serverValidation(UtilityBean<Object> utilityBean) throws ServerException {
        String accountIdentifier = utilityBean.getAccountIdentifier();
        AccountBean account = accountsDao.getAccountDetailsForIdentifier(accountIdentifier);
        if (account == null) {
            log.error("Account identifier is invalid");
            throw new ServerException("Account identifier is invalid");
        }
        String userId;
        try {
             userId = commonUtils.getUserId(utilityBean.getAuthToken());
        } catch (ControlCenterException ex) {
            log.error("Error while extracting userIdentifier from authorization token. Reason: Could be invalid authorization token");
            throw new ServerException("Error while extracting user details from authorization token");
        }
         return UtilityBean.builder().account(account).userId(userId).build();
    }

    public List<InstanceHealthDetails> process(UtilityBean<Object> utilityBean) throws DataProcessingException {
        List<InstanceHealthDetails> instanceHealthDetails = new ArrayList<>();
        AccountBean account = utilityBean.getAccount();
        try {
            int endTimeBracket = Integer.parseInt(Constants.TIME_BRACKET_DEF) * 60 * 1000;
            Timestamp timestamp = DateTimeUtil.getCurrentTimestampInGMT();
            if (Objects.isNull(timestamp)) {
                throw new DataProcessingException(UIMessages.INSTANCE_HEALTH_SERVICE_TIMESTAMP);
            }
            long currentTimeInGmt = timestamp.getTime();
            long timeBracketDiff = currentTimeInGmt - endTimeBracket;
            List<CompInstClusterDetails> compInstances = masterDataDao.getCompInstanceDetails(account.getId());
            compInstances = compInstances.stream()
                    .filter(c -> c.getIsCluster() == 0)
                    .filter(c -> c.getStatus() == 1)
                    .collect(Collectors.toList());

            if (compInstances.isEmpty()) {
                log.warn("No component instance data found for accountIdentifier [{}]", account.getId());
                return new ArrayList<>();
            }
            List<CompInstClusterMappingBean> instClusterMapping = componentDao.getInstanceClusterMapping(account.getId());
            Map<Integer, List<Integer>> instClusterIdMap = instClusterMapping.stream()
                    .collect(Collectors.groupingBy(CompInstClusterMappingBean::getCompInstanceId,
                            Collectors.mapping(CompInstClusterMappingBean::getClusterId, Collectors.toList())));
            Set<Integer> cIds = new HashSet<>();
            instClusterIdMap.values().forEach(cIds::addAll);
            UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(utilityBean.getAuthToken(), account.getIdentifier());
            if (userAccessDetails == null) {
                log.error("User access bean unavailable for user [{}] and account [{}]", account.getIdentifier());
                throw new DataProcessingException("Error while fetching user access bean for user");
            }
            List<ControllerBean> accessibleServiceList = null;
            accessibleServiceList = controllerDao.getControllerDetailsWithIdentifier(account.getId(), userAccessDetails.getServiceIdentifiers());

            if (accessibleServiceList.isEmpty()) {
                log.error("There are no application-service mapping available for this user.");
                return new ArrayList<>();
            }

            ViewTypesBean serviceType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.CONTROLLER_TYPE_NAME_DEFAULT,
                    Constants.SERVICES_CONTROLLER_TYPE);
            Map<String, String> servicesMap = accessibleServiceList.stream()
                    .filter(t -> t.getControllerTypeId() == serviceType.getSubTypeId())
                    .collect(Collectors.toMap(ControllerBean::getAppId, ControllerBean::getName));
            TagDetailsBean controllerTag = tagsDao.getTagDetails(Constants.CONTROLLER_TAG);
            Map<Integer, Set<String>> clusterServiceKeys = tagsDao.getTagMappingDetails(account.getId()).stream()
                    .filter(t -> t.getTagId() == controllerTag.getId())
                    /*the following filter is to ensure that there are no NULL elements added to the list of services*/
                    .filter(t -> servicesMap.containsKey(t.getTagKey()))
                    /*the following filter is to ensure that the check that the data is pulled only for clusters*/
                    .filter(t -> cIds.contains(t.getObjectId()))
                    .filter(t -> t.getObjectRefTable().equalsIgnoreCase(Constants.COMP_INSTANCE_TABLE))
                    .collect(Collectors.groupingBy(TagMappingDetails::getObjectId,
                            Collectors.mapping(t -> servicesMap.get(t.getTagKey()), Collectors.toSet())));
            compInstances.forEach(c -> {
                Set<String> services = new HashSet<>();
                List<Integer> clusterIds = instClusterIdMap.getOrDefault(c.getInstanceId(), new ArrayList<>());
                clusterIds.forEach(clusterId -> services.addAll(getServicesList(clusterServiceKeys, clusterId)));

                if (!services.contains("NA")) {
                    long instHealthTime = configureKpiCassandraDao.getInstanceHealthMapForAccount(account.getIdentifier(), c.getIdentifier());
                    InstanceHealthDetails healthDetails = new InstanceHealthDetails();
                    healthDetails.setInstanceName(c.getInstanceName());
                    healthDetails.setType(c.getComponentName());

                    healthDetails.setServices(services);
                    if (0L != instHealthTime && instHealthTime >= timeBracketDiff) {
                        healthDetails.setDataPostStatus(1);
                    }
                    healthDetails.setLastPostedTime(instHealthTime);
                    healthDetails.setId(c.getInstanceId());
                    healthDetails.setHost(c.getHostAddress());
                    instanceHealthDetails.add(healthDetails);
                }
            });
            return getSortedList(instanceHealthDetails);
        } catch (ControlCenterException e) {
            throw new DataProcessingException(Throwables.getRootCause(e).getMessage());
        }
    }

    private Set<String> getServicesList(Map<Integer, Set<String>> instanceServices, int clusterId) {
        Set<String> services = new HashSet<>();
        if(instanceServices.containsKey(clusterId)) {
            return instanceServices.get(clusterId);
        } else {
            services.add("NA");
            return services;
        }
    }

   private  List<InstanceHealthDetails> getSortedList(List<InstanceHealthDetails> instanceHealthDetails){
        if (Objects.isNull(instanceHealthDetails)){
            return new ArrayList<>();
        }
        instanceHealthDetails.sort(Comparator.comparing(InstanceHealthDetails::getDataPostStatus)
                .thenComparing(InstanceHealthDetails::getLastPostedTime, nullsFirst(Comparator.naturalOrder()))
                .thenComparing(InstanceHealthDetails::getInstanceName)
                .thenComparing(InstanceHealthDetails::getType));
        return instanceHealthDetails;
    }
}

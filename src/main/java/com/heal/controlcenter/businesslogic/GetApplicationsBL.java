package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ControllerDao;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.GetApplications;
import com.heal.controlcenter.pojo.UserAccessInfo;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.UserValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetApplicationsBL implements BusinessLogic<String, UserAccessInfo, List<GetApplications>> {

    @Autowired
    UserDao userDao;
    @Autowired
    AccountsDao accountsDao;
    @Autowired
    UserValidationUtil userValidationUtil;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    ControllerDao controllerDao;

    private boolean clusterDataRequired;

    @Override
    public UtilityBean<String> clientValidation(String requestBody, String... params) throws ClientException {
        String userId;

        try {
            userId = commonUtils.getUserId(params[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while fetching userId. Details: ", e);
            throw new ClientException("Error while fetching userId from the Authorization token");
        }

        if (null == userId) {
            log.error("Invalid authorization token. Reason: UserId is NULL.");
            throw new ClientException("Invalid authorization token");
        }

        String accountIdentifier = params[1];
        if(accountIdentifier == null || accountIdentifier.trim().isEmpty()) {
            log.error("Invalid account identifier. Reason: It is either NULL or empty");
            throw new ClientException("Invalid account identifier");
        }

        clusterDataRequired = Boolean.parseBoolean(params[2]);

        return UtilityBean.<String>builder()
                .pojoObject(userId)
                .accountIdentifier(accountIdentifier)
                .authToken(params[0])
                .build();
    }

    @Override
    public UserAccessInfo serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        String accountIdentifier = utilityBean.getAccountIdentifier();

        AccountBean account = accountsDao.getAccountDetailsForIdentifier(accountIdentifier);

        if (account == null) {
            log.error("Invalid account identifier [{}]", accountIdentifier);
            throw new ServerException("Invalid Account Identifier");
        }

        UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(utilityBean.getAuthToken(), account.getIdentifier());
        if(userAccessDetails == null) {
            log.error("User access details unavailable for user [{}]", utilityBean.getAuthToken());
            throw new ServerException("User access details unavailable");
        }

        List<String> userApps = userAccessDetails.getApplicationIdentifiers();
        if (userApps == null || userApps.isEmpty()) {
            log.error("No applications mapped to user [{}]", utilityBean.getAuthToken());
            throw new ServerException("No applications mapped to user " + utilityBean.getAuthToken());
        }

        List<ControllerBean> accountWiseApps = controllerDao.getApplicationsList(account.getId());
        if (accountWiseApps.isEmpty()) {
            log.error("Applications unavailable for account [{}]", account.getIdentifier());
            throw new ServerException("Application unavailable for account " + account.getIdentifier());
        }

        List<ControllerBean> accessibleApps = accountWiseApps.parallelStream()
                .filter(app -> userApps.contains(app.getIdentifier())).collect(Collectors.toList());

        if (accessibleApps.isEmpty()) {
            log.error("Applications mapped to user [{}] is unavailable in account [{}]", utilityBean.getAuthToken(), account.getIdentifier());
            throw new ServerException(String.format("Applications mapped to user [%s] is unavailable in account [%s]",
                    utilityBean.getAuthToken(), account.getIdentifier()));
        }

        return new UserAccessInfo(accessibleApps, userAccessDetails);
    }

    @Override
    public List<GetApplications> process(UserAccessInfo userAccessInfo) throws DataProcessingException {
        List<ViewApplicationServiceMappingBean> mappedServices = userAccessInfo.getUserAccessDetails().getApplicationServiceMappingBeans();

        return userAccessInfo.getAccessibleApplications().parallelStream()
                .map(c -> {
                    try {
                        return GetApplications.builder()
                                .id(c.getId())
                                .identifier(c.getIdentifier())
                                .name(c.getName())
                                .lastModifiedBy(userDao.getUsernameFromIdentifier(c.getLastModifiedBy()))
                                .lastModifiedOn(CommonUtils.getGMTToEpochTime(c.getUpdatedTime()))
                                .services(getMappedServices(mappedServices))
                                .build();
                    } catch (ControlCenterException e) {
                        log.error("Error in fetching information for application [{}] for userId [{}]", c.getIdentifier(), c.getLastModifiedBy());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<GetApplications.ServiceClusterDetails> getMappedServices(List<ViewApplicationServiceMappingBean> mappedServices) {
        if (!clusterDataRequired) {
            return mappedServices.parallelStream().map(t -> GetApplications.ServiceClusterDetails.builder()
                            .id(t.getServiceId())
                            .name(t.getServiceName())
                            .identifier(t.getServiceIdentifier())
                            .build())
                    .collect(Collectors.toList());
        }

        return mappedServices.parallelStream().map(service -> {
            List<GetApplications.ClusterComponentDetails> hostClusterComponentDetails;
            List<GetApplications.ClusterComponentDetails> componentClusterComponentDetails;
            try {
                hostClusterComponentDetails = controllerDao.getHostClusterComponentDetailsForService(service.getServiceIdentifier());
                componentClusterComponentDetails = controllerDao.getComponentClusterComponentDetailsForService(service.getServiceIdentifier());
            } catch (Exception e) {
                return null;
            }
            return GetApplications.ServiceClusterDetails.builder()
                    .id(service.getServiceId())
                    .name(service.getServiceName())
                    .identifier(service.getServiceIdentifier())
                    .hostCluster(hostClusterComponentDetails)
                    .componentCluster(componentClusterComponentDetails)
                    .build();
        }).collect(Collectors.toList());
    }
}


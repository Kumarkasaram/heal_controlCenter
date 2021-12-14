package com.heal.controlcenter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ControllerDao;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserValidationUtil {

    @Autowired
    private ControllerDao controllerDao;
    @Autowired
    private AccountsDao accountDao;

    private static final Gson gson = new GsonBuilder().create();

    public UserAccessDetails getUserAccessDetails(String userIdentifier, String accountIdentifier) {
        UserAccessBean accessDetails = accountDao.fetchUserAccessDetailsUsingIdentifier(userIdentifier);

        if (accessDetails == null) {
            log.error("User access bean unavailable for user [{}] and account [{}]", userIdentifier, accountIdentifier);
            return null;
        }

        UserAccessDetails userAccessDetails = getUserAccessibleApplicationsServices(accessDetails.getAccessDetails(), userIdentifier, accountIdentifier);

        if (userAccessDetails == null) {
            log.error("User access details unavailable for user [{}] and account [{}]", userIdentifier, accountIdentifier);
            return null;
        }

        return userAccessDetails;
    }

    public UserAccessDetails getUserAccessibleApplicationsServices(String accessDetails, String userIdentifier, String accountIdentifier) {
        UserAccessDetails userAccessDetails = null;

        Type userBeanType = new TypeReference<AccessDetailsBean>() {
        }.getType();

        AccessDetailsBean bean = gson.fromJson(accessDetails, userBeanType);

        if (bean != null && bean.getAccounts() != null) {
            if (bean.getAccounts().contains(accountIdentifier)) {
                int accountId = accountDao.getAccountByIdentifier(accountIdentifier).getId();

                Map<String, AccessDetailsBean.Application> accessibleApplications = bean.getAccountMapping();
                if (accessibleApplications == null || accessibleApplications.isEmpty()) {
                    log.error("There no applications mapped to account [{}] and user [{}]",
                            accountIdentifier, userIdentifier);
                    return null;
                }

                AccessDetailsBean.Application applicationIdentifiers = accessibleApplications.get(accountIdentifier);
                List<ControllerBean> applicationControllerList = controllerDao.getApplicationsList(accountId);

                if (!applicationIdentifiers.getApplications().contains("*")) {
                    applicationControllerList = applicationControllerList.parallelStream()
                            .filter(app -> (applicationIdentifiers.getApplications().contains(app.getIdentifier())))
                            .collect(Collectors.toList());

                }
                userAccessDetails = populateUserAccessDetails(accountId, applicationControllerList);

            } else if (bean.getAccounts().contains("*")) {
                int accountId = accountDao.getAccountByIdentifier(accountIdentifier).getId();
                List<ControllerBean> applicationControllerList = controllerDao.getApplicationsList(accountId);
                userAccessDetails = populateUserAccessDetails(accountId, applicationControllerList);
            }
        }

        return userAccessDetails;
    }

    private UserAccessDetails populateUserAccessDetails(int accountId, List<ControllerBean> applicationControllerList) {
        UserAccessDetails userAccessDetails = new UserAccessDetails();
        userAccessDetails.setApplicationIds(new ArrayList<>());
        userAccessDetails.setServiceIds(new ArrayList<>());
        userAccessDetails.setServiceIdentifiers(new ArrayList<>());
        userAccessDetails.setTransactionIds(new ArrayList<>());
        userAccessDetails.setAgents(new ArrayList<>());
        userAccessDetails.setApplicationIdentifiers(new ArrayList<>());

        if(applicationControllerList.isEmpty()) {
            return userAccessDetails;
        }

        List<ViewApplicationServiceMappingBean> beans = getServicesMappedToApplications(accountId, applicationControllerList);
        userAccessDetails.setApplicationIds(beans.parallelStream().map(ViewApplicationServiceMappingBean::getApplicationId).distinct().collect(Collectors.toList()));
        userAccessDetails.setApplicationIdentifiers(beans.parallelStream().map(ViewApplicationServiceMappingBean::getApplicationIdentifier).distinct().collect(Collectors.toList()));
        userAccessDetails.setServiceIds(beans.parallelStream().map(ViewApplicationServiceMappingBean::getServiceId).filter(serviceId -> serviceId != null && serviceId > 0).distinct().collect(Collectors.toList()));
        userAccessDetails.setServiceIdentifiers(beans.parallelStream().map(ViewApplicationServiceMappingBean::getServiceIdentifier).distinct().collect(Collectors.toList()));

        return userAccessDetails;
    }

    private List<ViewApplicationServiceMappingBean> getServicesMappedToApplications(int accountId, List<ControllerBean> applicationControllerList) {
        if(applicationControllerList.isEmpty()) {
            return Collections.emptyList();
        }

        return applicationControllerList.parallelStream()
                .map(controller -> {
                    List<ViewApplicationServiceMappingBean> services = controllerDao.getServicesMappedToApplication(accountId, controller.getIdentifier());
                    if(!services.isEmpty()) {
                        return services;
                    }
                    return Collections.singleton(ViewApplicationServiceMappingBean.builder()
                            .applicationId(controller.getId())
                            .applicationIdentifier(controller.getIdentifier())
                            .applicationName(controller.getName())
                            .build());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public  List<ControllerBean> getAccessibleApplicationsForUser(String userIdentifier, String accountIdentifier) {
        UserAccessBean accessDetails;
        accessDetails = accountDao.fetchUserAccessDetailsUsingIdentifier(userIdentifier);

        if (accessDetails == null) {
            log.error("User access bean unavailable for user [{}] and account [{}]", userIdentifier, accountIdentifier);
            return new ArrayList<>();
        }
        Type userBeanType = new TypeToken<AccessDetailsBean>() {
        }.getType();
        AccessDetailsBean bean = CommonUtils.jsonToObject(accessDetails.getAccessDetails(), userBeanType);
        if (bean == null || bean.getAccounts() == null) {
            log.error("Access details unavailable for user [{}]", userIdentifier);
            return new ArrayList<>();
        }

        if (bean.getAccounts().contains(accountIdentifier)) {
            int accessibleAccountId = accountDao.getAccountByIdentifier(accountIdentifier).getId();
            Map<String, AccessDetailsBean.Application> accessibleApplications = bean.getAccountMapping();

            if (accessibleApplications == null || accessibleApplications.isEmpty()) {
                log.error("There no applications mapped to account [{}] and user [{}]",
                        accountIdentifier, userIdentifier);
                return new ArrayList<>();
            }

            AccessDetailsBean.Application applicationIdentifiers = accessibleApplications.get(accountIdentifier);
            List<ControllerBean> applicationControllerList = controllerDao.getApplicationsList(accessibleAccountId);

            if (applicationIdentifiers.getApplications().contains("*")) {
                return applicationControllerList;
            } else {
                return applicationControllerList.parallelStream()
                        .filter(app -> (applicationIdentifiers.getApplications().contains(app.getIdentifier())))
                        .collect(Collectors.toList());

            }
        } else if (bean.getAccounts().contains("*")) {
            int accessibleAccountId = accountDao.getAccountByIdentifier(accountIdentifier).getId();
            return controllerDao.getApplicationsList(accessibleAccountId);
        }

        return new ArrayList<>();
    }

}

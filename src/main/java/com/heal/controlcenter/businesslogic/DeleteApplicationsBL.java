package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ControllerDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.StringUtils;
import com.heal.controlcenter.util.UserValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeleteApplicationsBL {

    @Autowired
    private AccountsDao accountsDao;
    @Autowired
    private ControllerDao controllerDao;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private UserValidationUtil userValidationUtil;

    public UtilityBean<List<String>> clientValidation(String[] appIdentifiers, String... params) throws ClientException {

        String authKey = params[0];
        if (authKey == null || authKey.trim().isEmpty()) {
            log.error("Invalid authorization token. Reason: It is either NULL or empty");
            throw new ClientException("Invalid authorization token");
        }

        String user;
        try {
            user = commonUtils.getUserId(authKey);
        } catch (Exception e) {
            log.error("Unable to fetch userId from authorization token");
            throw new ClientException("Unable to fetch userId from authorization token");
        }

        String identifier = params[1];
        if (identifier == null || identifier.trim().isEmpty()) {
            log.error("Invalid account identifier. Reason: It is either NULL or empty");
            throw new ClientException("Invalid account identifier");
        }

        if (appIdentifiers == null || appIdentifiers.length == 0 || StringUtils.isEmpty(appIdentifiers[0])) {
            log.error("appIdentifier should not be empty or null in the query parameter.");
            throw new ClientException("appIdentifier should not be empty or null in the query parameter.");
        }

        List<String> applications = Arrays.asList(appIdentifiers[0].split(","));

        applications = applications.parallelStream().distinct().collect(Collectors.toList());

        for (String app : applications) {
            if (StringUtils.isEmpty(app)) {
                log.error("Found empty/null Application(s) name in request parameter.");
                throw new ClientException("Found empty/null Application(s) name in request parameter.");
            }
        }

        return UtilityBean.<List<String>>builder()
                .accountIdentifier(identifier)
                .userId(user)
                .pojoObject(applications)
                .build();
    }

    public List<ControllerBean> serverValidation(UtilityBean<List<String>> utilityBean) throws ServerException {
        String accountIdentifier = utilityBean.getAccountIdentifier();

        AccountBean account = accountsDao.getAccountDetailsForIdentifier(accountIdentifier);

        if (account == null) {
            log.error("Invalid account identifier [{}]", accountIdentifier);
            throw new ServerException("Invalid Account Identifier");
        }

        int accountId = account.getId();

        UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(utilityBean.getUserId(), accountIdentifier);
        List<String> accessibleApps = userAccessDetails.getApplicationIdentifiers();
        Map<String, List<ViewApplicationServiceMappingBean>> accessibleAppVsServices = userAccessDetails.getApplicationServiceMappingBeans().parallelStream()
                .collect(Collectors.groupingBy(ViewApplicationServiceMappingBean::getApplicationIdentifier));

        List<ControllerBean> accessibleControllers = controllerDao.getApplicationsList(accountId).parallelStream()
                .filter(c -> accessibleApps.contains(c.getIdentifier())).collect(Collectors.toList());

        for (String app : utilityBean.getPojoObject()) {
            ControllerBean controller = accessibleControllers.parallelStream().filter(c -> (c.getIdentifier().equals(app.trim()))
                    && c.getStatus() == 1).findAny().orElse(null);
            if (controller == null) {
                log.error("Application with Identifier '[{}]' does not exist for current user and account.", app);
                throw new ServerException(String.format("Application with Identifier '[%s]' does not exist for current user and account.", app));
            }

            List<ViewApplicationServiceMappingBean> mappedServices = accessibleAppVsServices.getOrDefault(app, new ArrayList<>());
            if (!mappedServices.isEmpty()) {
                log.error("Some services are still mapped to Application with Identifier '[{}]'." +
                        " Please remove the mapped services first.", app);
                throw new ServerException(String.format("Some services are still mapped to Application with Identifier '[%s]'." +
                        " Please remove the mapped services first.", app));
            }
            accessibleControllers.add(controller);
        }
        return accessibleControllers;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Object process(List<ControllerBean> controllerBeanList) throws DataProcessingException {
        for (ControllerBean bean : controllerBeanList) {
            try {
                checkExecutionStatus(controllerDao.deleteApplicationNotificationMappingWithAppId(bean.getId()), bean.getIdentifier(), "application_notification_mapping");
                checkExecutionStatus(controllerDao.deleteUserNotificationMappingWithAppId(bean.getId()), bean.getIdentifier(), "user_notification_mapping");
                checkExecutionStatus(controllerDao.deleteApplicationPercentilesWithAppId(bean.getId()), bean.getIdentifier(), "application_percentiles");
                checkExecutionStatus(controllerDao.deleteControllerWithId(bean.getId()), bean.getIdentifier(), "controller");
            } catch (ControlCenterException e) {
                throw new DataProcessingException(e.getMessage());
            }
        }
        return null;
    }

    private void checkExecutionStatus(int res, String appIdentifier, String tableName) throws ControlCenterException {
        if (res == -1) {
            log.error("Error occurred while deleting from '[{}]' table for application with identifier '[{}]'.", tableName, appIdentifier);
            throw new ControlCenterException(String.format("Error occurred while deleting from '[%s]' table for application with " +
                    "identifier '[%s]'.", tableName, appIdentifier));
        } else if (res == 0) {
            log.warn("No Entry found in '[{}]' table for application with identifier '[{}]'.", tableName, appIdentifier);
        }
    }
}

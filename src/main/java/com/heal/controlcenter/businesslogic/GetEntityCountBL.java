package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.enums.SetupTypes;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.UIMessages;
import com.heal.controlcenter.util.UserValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GetEntityCountBL implements BusinessLogic<InstancesBean, InstancesBean, Map<String, Object>> {

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    ActionScriptDao actionScriptDao;
    @Autowired
    KPIDao kpiDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    AccountsDao accountDao;
    @Autowired
    UserValidationUtil userValidationUtil;

    @Override
    public UtilityBean<InstancesBean> clientValidation(InstancesBean requestBody, String... requestParams) throws ClientException {
        String userId;
        SetupTypes types;
        String authorization = requestParams[0];
        String accountIdentifier = requestParams[1];
        String typeName = requestParams[2];

        if (authorization.isEmpty()) {
            log.error(UIMessages.AUTH_KEY_EMPTY);
            throw new ClientException(UIMessages.AUTH_KEY_EMPTY);
        }

        if (accountIdentifier.isEmpty()) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
            throw new ClientException(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
        }

        if (typeName == null || typeName.isEmpty()) {
            log.error("Type should not be empty or null");
            throw new ClientException("Invalid type name.");
        }

        try {
            userId = commonUtils.getUserId(authorization);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while fetching userId. Reason: {}", e.getMessage(), e);
            throw new ClientException("Error while fetching userId from the Authorization token");
        }

        if(userId == null) {
            log.error("Invalid Authorization token. Reason: UserId is NULL.");
            throw new ClientException("Invalid Authorization token");
        }

        try {
            types = SetupTypes.valueOf(typeName.trim());
        } catch (IllegalArgumentException ie) {
            log.error("Invalid type name provided.");
            throw new ClientException("Invalid type name.");
        }

        InstancesBean instancesBean = new InstancesBean();
        instancesBean.setTypeName(typeName);
        instancesBean.setUser(userId);
        instancesBean.setType(types);

        return UtilityBean.<InstancesBean>builder()
                .accountIdentifier(accountIdentifier)
                .authToken(authorization)
                .pojoObject(instancesBean)
                .build();
    }

    @Override
    public InstancesBean serverValidation(UtilityBean<InstancesBean> utilityBean) throws ServerException {
        AccountBean account = accountDao.getAccountByIdentifier(utilityBean.getAccountIdentifier());
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        InstancesBean instancesBean = utilityBean.getPojoObject();
        instancesBean.setAccountIdentifier(account.getIdentifier());
        instancesBean.setAccountId(account.getId());

        return instancesBean;
    }

    @Override
    public Map<String, Object> process(InstancesBean bean) throws DataProcessingException {
        Map<String, Object> data = new HashMap<>();
        try {
            if (bean.getType().equals(SetupTypes.HOST)) {
                List<CompInstClusterDetailsBean> details = masterDataDao.getCompInstanceDetails(bean.getAccountId());
                long count = details.stream().filter(i -> i.getComponentTypeName().equalsIgnoreCase(SetupTypes.HOST.name())).count();
                data.put(Constants.TOTAL, count);
            } else if (bean.getType().equals(SetupTypes.SERVICE)) {
                UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(bean.getUser(), bean.getAccountIdentifier());
                if (userAccessDetails == null) {
                    data.put(Constants.TOTAL, 0);
                    return data;
                }
                data.put(Constants.TOTAL, userAccessDetails.getServiceIds().size());
            } else if (bean.getType().equals(SetupTypes.APPLICATION)) {
                UserAccessDetails userAccessDetails = userValidationUtil.getUserAccessDetails(bean.getUser(), bean.getAccountIdentifier());
                data.put(Constants.TOTAL, userAccessDetails.getApplicationIdentifiers().size());
            } else if (bean.getType().equals(SetupTypes.METRICES)) {
                long count = kpiDao.getKpiCountForAccount(bean.getAccountId());
                data.put(Constants.TOTAL, count);
            } else if (bean.getType().equals(SetupTypes.CATEGORY)) {
                data.put(Constants.TOTAL, categoryDao.getCategoryCountForAccount(bean.getAccountId()));
            } else if (bean.getType().equals(SetupTypes.FORENSIC)) {
                data.put(Constants.TOTAL, actionScriptDao.getForensicCountForAccount(bean.getAccountId()));
            }

            return data;
        } catch (Exception e) {
            log.error("Error occurred, couldn't fetch entity count. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
            throw new DataProcessingException(e.getMessage());
        }
    }
}

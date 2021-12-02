package com.heal.controlcenter.businesslogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.base.Throwables;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.ComponentDetails;
import com.heal.controlcenter.beans.MasterComponentBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.beans.VersionBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.IdPojo;
import com.heal.controlcenter.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetComponentDetailsBL implements BusinessLogic<Object, Integer, List<ComponentDetails>> {
	@Autowired		
	AccountsDao accountsDao;
	@Autowired
	CommonUtils commonUtils;
	@Autowired
	MasterDataDao masterDataDao;
	
    public UtilityBean<Object> clientValidation(Object requestBody,String... requestObject ) throws ClientException {
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

    public Integer serverValidation(UtilityBean<Object> utilityBean) throws ServerException {

        String accountIdentifier = utilityBean.getAccountIdentifier();

        AccountBean account = accountsDao.getAccountDetailsForIdentifier(accountIdentifier);

        if (account == null) {
            log.error("Account identifier is invalid");
            throw new ServerException("Account identifier is invalid");
        }

        int accountId = account.getId();

        try {
        	String userId = commonUtils.getUserId(utilityBean.getAuthToken());
        } catch (ControlCenterException ex) {
        	 log.error("Error while extracting userIdentifier from authorization token. Reason: Could be invalid authorization token");
             throw new ServerException("Error while extracting user details from authorization token");
        }

        return accountId;
    }

    public List<ComponentDetails> process(Integer accountId) throws DataProcessingException {
        try {

            List<ComponentDetails> componentDetailsList = new ArrayList<>();

            List<MasterComponentBean> componentsList = masterDataDao.getComponentMasterDataForAccountId(accountId);

            Map<ComponentDetails, List<MasterComponentBean>> componentDetailsMap = componentsList.parallelStream()
                    .collect(Collectors.groupingBy(c -> ComponentDetails.builder().id(c.getId()).name(c.getName()).build()));

            componentDetailsMap.forEach((componentDetails, masterComponentBeans) -> {
                Set<IdPojo> componentTypes = new HashSet<>();
                Set<VersionBean> commonVersions = new HashSet<>();
                masterComponentBeans.forEach(component -> {
                    componentTypes.add(IdPojo.builder().id(component.getComponentTypeId())
                            .name(component.getComponentTypeName()).build());
                    commonVersions.add(VersionBean.builder().id(component.getCommonVersionId())
                            .version(component.getCommonVersionName()).build());
                });
                componentDetails.setComponentTypes(componentTypes);
                componentDetails.setCommonVersions(commonVersions);
                componentDetailsList.add(componentDetails);
            });

            return componentDetailsList;
        } catch (Exception e) {
            throw new DataProcessingException(Throwables.getRootCause(e).getMessage());
        }
    }

}

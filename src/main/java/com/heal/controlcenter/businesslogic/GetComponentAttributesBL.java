package com.heal.controlcenter.businesslogic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.AttributesList;
import com.heal.controlcenter.beans.CommonVersionAttributes;
import com.heal.controlcenter.beans.ComponentAttributesMapping;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.beans.ViewComponentAttributesPojo;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ComponentDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.IdPojo;
import com.heal.controlcenter.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetComponentAttributesBL implements BusinessLogic<Object, Integer, List<ComponentAttributesMapping>> {
	@Autowired		
	AccountsDao accountsDao;
	@Autowired
	CommonUtils commonUtils;
	@Autowired
	ComponentDao componentDao;
	
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

    public List<ComponentAttributesMapping> process(Integer accountId) throws DataProcessingException {
        try {

            List<ViewComponentAttributesPojo> viewComponentAttributesList = componentDao.getComponentAttributeDetails();
            if (viewComponentAttributesList == null) {
                log.error("No Component-Attributes data found.");
                throw new DataProcessingException("No Component-Attributes data found.");
            }

            List<ComponentAttributesMapping> componentAttributesMappings = viewComponentAttributesList.parallelStream()
                    .map(c -> ComponentAttributesMapping.builder()
                            .id(c.getComponentId())
                            .name(c.getComponentName())
                            .type(c.getComponentTypeName())
                            .commonVersion(viewComponentAttributesList.parallelStream()
                                    .filter(d -> d.getComponentId() == c.getComponentId())
                                    .map(d -> CommonVersionAttributes.builder()
                                            .id(d.getCommonVersionId())
                                            .name(d.getCommonVersionName())
                                            .build())
                                    .distinct()
                                    .collect(Collectors.toList()))
                            .build())
                    .distinct()
                    .sorted(Comparator.comparing(ComponentAttributesMapping::getName))
                    .collect(Collectors.toList());

            for (ComponentAttributesMapping componentAttributesMapping : componentAttributesMappings) {
                for(CommonVersionAttributes commonVersionAttributes : componentAttributesMapping.getCommonVersion()){
                    commonVersionAttributes.setComponentVersion(viewComponentAttributesList.parallelStream()
                            .filter(two -> componentAttributesMapping.getId() == two.getComponentId() &&
                                    commonVersionAttributes.getId() == two.getCommonVersionId())
                            .map(c -> IdPojo.builder()
                                    .id(c.getComponentVersionId())
                                    .name(c.getComponentVersionName())
                                    .build())
                            .distinct()
                            .collect(Collectors.toList()));

                    commonVersionAttributes.setAttributes(viewComponentAttributesList.parallelStream()
                            .filter(two -> componentAttributesMapping.getId() == two.getComponentId() &&
                                    commonVersionAttributes.getId() == two.getCommonVersionId())
                            .map(d -> AttributesList.builder()
                                    .id(d.getAttributeId())
                                    .name(d.getAttributeName())
                                    .defaultValue(d.getDefaultValue())
                                    .isMandatory(d.getIsMandatory())
                                    .build())
                            .distinct()
                            .collect(Collectors.toList()));

                }
            }

            return componentAttributesMappings;
        } catch (Exception e) {
            throw new DataProcessingException(Throwables.getRootCause(e).getMessage());
        }
    }

}

package com.heal.controlcenter.businesslogic;

import java.util.List;
import java.util.stream.Collectors;

import com.heal.controlcenter.beans.MasterSubTypeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;
import com.heal.controlcenter.Common.UIMessages;
import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.AgentTypePojo;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetAgentTypeAtAccLvlBL implements BusinessLogic<Object, Integer, List<AgentTypePojo>> {

	@Autowired		
	AccountsDao accountsDao;
	@Autowired
	CommonUtils commonUtils;
	@Autowired
	MasterDataDao masterDataDao;
	
    public UtilityBean<Object> clientValidation(Object requestBody,String... params) throws ClientException {
        if (params == null) {
            log.error(UIMessages.REQUEST_NULL);
            throw new ClientException(UIMessages.REQUEST_NULL);
        }

        String authKey = params[0];
        if (authKey == null || authKey.trim().isEmpty()) {
            log.error("Invalid authorization token. Reason: It is either NULL or empty");
            throw new ClientException("Invalid authorization token");
        }

        String identifier = params[1];
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
            log.error("Invalid account identifier [{}]", accountIdentifier);
            throw new ServerException("Invalid Account Identifier");
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

    public List<AgentTypePojo> process(Integer accountId) throws DataProcessingException {
        try {
            List<MasterSubTypeBean> masterSubTypeBeans = masterDataDao.getMasterSubTypeDetailsList();
            if (masterSubTypeBeans == null) {
                log.error("No Agent type data found.");
                throw new DataProcessingException("No Agent type data found.");
            }
            return masterSubTypeBeans.parallelStream()
                    .map(c -> AgentTypePojo.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .build()).collect(Collectors.toList());
                    
        } catch (Exception e) {
            throw new DataProcessingException(Throwables.getRootCause(e).getMessage());
        }
    }

}

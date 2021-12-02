package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.SMSDetailsBean;
import com.heal.controlcenter.beans.SMSParameterBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.dao.mysql.NotificationsDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.SMSDetailsPojo;
import com.heal.controlcenter.pojo.SMSParameterPojo;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetSMSConfigurationsBL implements BusinessLogic<Object, Integer, SMSDetailsPojo> {

    @Autowired
    AccountsDao accountDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    NotificationsDao notificationsDao;

    @Override
    public UtilityBean<Object> clientValidation(Object requestBody, String... requestParams) throws ClientException {
        String userId;

        if (requestParams[0].isEmpty()) {
            log.error(UIMessages.AUTH_KEY_EMPTY);
            throw new ClientException(UIMessages.AUTH_KEY_EMPTY);
        }

        if (requestParams[1].isEmpty()) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
            throw new ClientException(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
        }

        try {
            userId = commonUtils.getUserId(requestParams[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while getting UserId. Details: {}", e.getMessage());
            throw new ClientException("Error occurred while fetching UserId.");
        }
        if (userId == null) {
            log.error(UIMessages.AUTH_KEY_INVALID);
            throw new ClientException(UIMessages.AUTH_KEY_INVALID);
        }

        return UtilityBean.builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .build();
    }

    @Override
    public Integer serverValidation(UtilityBean<Object> utilityBean) throws ServerException {
        String accountIdentifier = utilityBean.getAccountIdentifier();

        AccountBean account = accountDao.getAccountByIdentifier(accountIdentifier);
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        return account.getId();
    }

    @Override
    public SMSDetailsPojo process(Integer accountId) throws DataProcessingException {
        SMSDetailsBean smsDetails = notificationsDao.getSMSDetails(accountId);
        if (smsDetails == null) {
            log.info("SMS Configurations not found for accountId [{}].", accountId);
            return null;
        }

        List<SMSParameterBean> smsParametersList = notificationsDao.getSMSParameters(smsDetails.getId());

        if (smsParametersList.isEmpty()) {
            log.info("SMS parameters not found for SMS details id [{}].", smsDetails.getId());
            return null;
        }

        List<SMSParameterPojo> smsParameters = smsParametersList.stream().map(parameter -> SMSParameterPojo.builder()
                        .parameterId(parameter.getId())
                        .parameterName(parameter.getParameterName())
                        .parameterValue(parameter.getParameterValue())
                        .parameterType(masterDataDao.getMstSubTypeBySubTypeId(parameter.getParameterTypeId()).getSubTypeName())
                        .isPlaceholder(parameter.getIsPlaceholder() == 1)
                        .build())
                .collect(Collectors.toList());

        return SMSDetailsPojo.builder()
                .address(smsDetails.getAddress())
                .id(smsDetails.getId())
                .countryCode(smsDetails.getCountryCode())
                .port(smsDetails.getPort())
                .protocolName(masterDataDao.getMstSubTypeBySubTypeId(smsDetails.getProtocolId()).getSubTypeName())
                .httpMethod(smsDetails.getHttpMethod())
                .httpRelativeUrl(smsDetails.getHttpRelativeUrl())
                .isMultiRequest(smsDetails.getIsMultiRequest())
                .postData(smsDetails.getPostData())
                .parameters(smsParameters)
                .build();
    }
}

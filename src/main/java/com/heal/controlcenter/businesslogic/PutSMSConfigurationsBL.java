package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
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
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PutSMSConfigurationsBL implements BusinessLogic<SMSDetailsPojo, UtilityBean<SMSDetailsPojo>, Object> {

    @Autowired
    AccountsDao accountDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    DateTimeUtil dateTimeUtil;
    @Autowired
    NotificationsDao notificationsDao;

    private static final String ADD_LIST = "AddList";
    private static final String MODIFY_LIST = "ModifyList";
    private static final String DELETE_LIST = "DeleteList";

    private ViewTypesBean protocolType;
    private ViewTypesBean parameterType;

    @Override
    public UtilityBean<SMSDetailsPojo> clientValidation(SMSDetailsPojo smsDetails, String... requestParams) throws ClientException {
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

        Map<String, String> error = smsDetails.validate();
        if (!error.isEmpty()) {
            String err = error.toString();
            log.error(err);
            throw new ClientException(err);
        }

        return UtilityBean.<SMSDetailsPojo>builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .pojoObject(smsDetails)
                .build();
    }

    @Override
    public UtilityBean<SMSDetailsPojo> serverValidation(UtilityBean<SMSDetailsPojo> utilityBean) throws ServerException {
        AccountBean account = accountDao.getAccountByIdentifier(utilityBean.getAccountIdentifier());
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }
        utilityBean.setAccount(account);

        SMSDetailsBean smsDetailsBeanExists = notificationsDao.getSMSDetails(account.getId());
        if (smsDetailsBeanExists == null) {
            throw new ServerException(String.format("Sms configuration is unavailable for accountId [%d]", account.getId()));
        }

        try {
            validationsForSMSDetailsPojo(utilityBean);
        } catch (ControlCenterException e) {
            throw new ServerException(e.getMessage());
        }

        return utilityBean;
    }

    @Override
    @Transactional
    public Object process(UtilityBean<SMSDetailsPojo> smsUtilityBean) throws DataProcessingException {
        Date time = dateTimeUtil.getCurrentTimestampInGMT();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME);
        String createdTime = dateFormat.format(time);
        SMSDetailsPojo smsDetails = smsUtilityBean.getPojoObject();

        SMSDetailsBean smsDetailsBean = SMSDetailsBean.builder()
                .accountId(smsUtilityBean.getAccount().getId())
                .address(smsDetails.getAddress())
                .countryCode(smsDetails.getCountryCode())
                .httpMethod(smsDetails.getHttpMethod())
                .httpRelativeUrl(smsDetails.getHttpRelativeUrl())
                .port(smsDetails.getPort())
                .postData(smsDetails.getHttpMethod().equalsIgnoreCase("POST") ? smsDetails.getPostData() : null)
                .postDataFlag(smsDetails.getHttpMethod().equalsIgnoreCase("POST") ? 1 : 0)
                .protocolId(protocolType.getSubTypeId())
                .lastModifiedBy(smsUtilityBean.getUserId())
                .createdTime(createdTime)
                .updatedTime(createdTime)
                .status(1)
                .isMultiRequest(smsDetails.getIsMultiRequest())
                .build();

        // Update SMS details
        try {
            notificationsDao.updateSMSDetails(smsDetailsBean);
        } catch (Exception e) {
            throw new DataProcessingException("Error occurred, couldn't update SMS details.");
        }

        // Update SMS parameters
        Map<String, List<SMSParameterBean>> smsParametersBeanMap = createSMSParameterList(smsDetails.getId(), smsDetails.getParameters(), smsUtilityBean.getUserId(), createdTime);

        List<SMSParameterBean> addSMSParametersList = smsParametersBeanMap.getOrDefault(ADD_LIST, new ArrayList<>());
        if (!addSMSParametersList.isEmpty()) {
            try {
                notificationsDao.addSMSParameter(addSMSParametersList);
            } catch (Exception e) {
                throw new DataProcessingException("Error occurred, couldn't add SMS parameter.");
            }
        }

        List<SMSParameterBean> modifySMSParametersList = smsParametersBeanMap.getOrDefault(MODIFY_LIST, new ArrayList<>());
        if (!modifySMSParametersList.isEmpty()) {
            try {
                notificationsDao.updateSMSParameters(modifySMSParametersList);
            } catch (Exception e) {
                throw new DataProcessingException("Error occurred, couldn't update SMS parameters.");
            }
        }

        List<SMSParameterBean> deleteSMSParametersList = smsParametersBeanMap.getOrDefault(DELETE_LIST, new ArrayList<>());
        if (!deleteSMSParametersList.isEmpty()) {
            try {
                notificationsDao.deleteSmsParameters(deleteSMSParametersList);
            } catch (Exception e) {
                throw new DataProcessingException("Error occurred, couldn't delete SMS parameters.");
            }
        }
        return null;
    }

    private void validationsForSMSDetailsPojo(UtilityBean<SMSDetailsPojo> utilityBean) throws ServerException, ControlCenterException {
        SMSDetailsPojo smsDetails = utilityBean.getPojoObject();

        protocolType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SMS_PROTOCOLS, smsDetails.getProtocolName());
        if (protocolType == null) {
            log.error("SMS protocol details unavailable for protocol name [{}].", smsDetails.getProtocolName());
            throw new ServerException("Error occurred while getting SMS protocol details.");
        }

        if (protocolType.getSubTypeName().equalsIgnoreCase("HTTP")) {
            ViewTypesBean httpMethodType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SMS_HTTP_METHODS, smsDetails.getHttpMethod());
            if (httpMethodType == null) {
                log.error("HTTP method type details unavailable for http method [{}]", smsDetails.getHttpMethod());
                throw new ServerException("Error occurred while getting HTTP method details.");
            }
            if (httpMethodType.getSubTypeName().equalsIgnoreCase("GET")) {
                smsDetails.setPostData("");
            }
        } else if (protocolType.getSubTypeName().equalsIgnoreCase("TCP")) {
            smsDetails.setPostData("");
            smsDetails.setHttpMethod("");
            smsDetails.setHttpRelativeUrl("");
            smsDetails.setIsMultiRequest(0);
        }

        List<SMSParameterBean> smsParameterBeans = notificationsDao.getSMSParameters(smsDetails.getId());
        if (smsParameterBeans == null || smsParameterBeans.isEmpty()) {
            log.error("SMS parameters not found for SMS details id [{}] for accountId [{}].", smsDetails.getId(), utilityBean.getAccount().getId());
            throw new ServerException("Error occurred, SMS parameter details not found.");
        }

        for (SMSParameterPojo smsParameter : smsDetails.getParameters()) {
            validateParameter(utilityBean.getAccount().getId(), smsParameter, smsParameterBeans);
        }
    }

    private void validateParameter(int accountId, SMSParameterPojo smsParameter, List<SMSParameterBean> smsParameterBeans) throws ServerException, ControlCenterException {
        parameterType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SMS_PARAMETER_TYPE_NAME,
                smsParameter.getParameterType());
        if (parameterType == null) {
            log.error("Parameter type details unavailable for parameter type [{}].", smsParameter.getParameterType());
            throw new ServerException("Error occurred while getting parameter type details.");
        }

        if (smsParameter.getIsPlaceholder().equals(Boolean.TRUE)) {
            ViewTypesBean parameterPlaceHolderType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SMS_PLACEHOLDERS,
                    smsParameter.getParameterValue());
            if (parameterPlaceHolderType == null) {
                log.error("Sub type details not found for the given parameter name type [{}].", smsParameter.getParameterName());
                throw new ServerException("Error occurred while getting sub type details.");
            }
        }

        ViewTypesBean finalParameterType = parameterType;
        // action validation
        if (smsParameter.getAction().equalsIgnoreCase("ADD")) {
            Optional<SMSParameterBean> alreadyPresentSmsParam = smsParameterBeans.stream()
                    .filter(smsParam -> (smsParam.getParameterName().equalsIgnoreCase(smsParameter.getParameterName())) &&
                            (smsParam.getParameterValue().equalsIgnoreCase(smsParameter.getParameterValue())) &&
                            (smsParam.getParameterTypeId() == finalParameterType.getSubTypeId()) &&
                            ((smsParam.getIsPlaceholder() == 0 && smsParameter.getIsPlaceholder().equals(Boolean.FALSE))
                                    || (smsParam.getIsPlaceholder() == 1 && smsParameter.getIsPlaceholder().equals(Boolean.TRUE))))
                    .findAny();
            if (alreadyPresentSmsParam.isPresent()) {
                log.error("SMS parameter is already present for accountId [{}] for parameterName [{}], parameterValue [{}].", accountId, smsParameter.getParameterName(), smsParameter.getParameterValue());
                throw new ServerException("Error occurred, SMS parameter already present for account.");
            }
        } else {
            Optional<SMSParameterBean> smsParameterBean = smsParameterBeans.stream()
                    .filter(smsParam -> (smsParam.getId() == smsParameter.getParameterId())).findAny();
            if (!smsParameterBean.isPresent()) {
                log.error("SMS parameter id [{}] is not valid.", smsParameter.getParameterId());
                throw new ServerException("Error occurred, Invalid SMS parameter id.");
            }

            if (smsParameter.getAction().equalsIgnoreCase("DELETE")) {
                List<SMSParameterBean> smsParams = Collections.singletonList(smsParameterBean.get());
                Optional<SMSParameterBean> deleteBean = smsParams.stream()
                        .filter(smsParam -> (smsParam.getParameterName().equalsIgnoreCase(smsParameter.getParameterName())) &&
                                (smsParam.getParameterValue().equalsIgnoreCase(smsParameter.getParameterValue())) &&
                                (smsParam.getParameterTypeId() == finalParameterType.getSubTypeId()) &&
                                ((smsParam.getIsPlaceholder() == 0 && smsParameter.getIsPlaceholder().equals(Boolean.FALSE))
                                        || (smsParam.getIsPlaceholder() == 1 && smsParameter.getIsPlaceholder().equals(Boolean.TRUE))))
                        .findAny();
                if (!deleteBean.isPresent()) {
                    log.error("Invalid request data for SMS parameter [action : DELETE] where parameterId [{}].", smsParameterBean.get().getId());
                    throw new ServerException("Error occurred, invalid request data for SMS parameter.");
                }
            }
        }
    }

    private Map<String,List<SMSParameterBean>> createSMSParameterList(int smsDetailsId, List<SMSParameterPojo> smsParameterList,
                                                                      String userId, String createdTime) {
        Map<String,List<SMSParameterBean>> result = new HashMap<>();
        List<SMSParameterBean> addSmsParamList = new ArrayList<>();
        List<SMSParameterBean> modifySmsParamList = new ArrayList<>();
        List<SMSParameterBean> delSmsParamList = new ArrayList<>();

        for (SMSParameterPojo smsParameter : smsParameterList) {
            SMSParameterBean smsBean = SMSParameterBean.builder()
                    .smsDetailsId(smsDetailsId)
                    .parameterName(smsParameter.getParameterName())
                    .parameterValue(smsParameter.getParameterValue())
                    .parameterTypeId(parameterType.getSubTypeId())
                    .createdTime(createdTime)
                    .updatedTime(createdTime)
                    .lastModifiedBy(userId)
                    .isPlaceholder(smsParameter.getIsPlaceholder().equals(Boolean.TRUE) ? 1 : 0)
                    .build();
            if (smsParameter.getAction().equalsIgnoreCase("add")) {
                addSmsParamList.add(smsBean);
            } else if (smsParameter.getAction().equalsIgnoreCase("edit")) {
                smsBean.setId(smsParameter.getParameterId());
                modifySmsParamList.add(smsBean);
            } else {
                smsBean.setId(smsParameter.getParameterId());
                delSmsParamList.add(smsBean);
            }
        }
        result.put(ADD_LIST, addSmsParamList);
        result.put(MODIFY_LIST, modifySmsParamList);
        result.put(DELETE_LIST, delSmsParamList);

        return result;
    }
}

package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.SMTPDetailsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.beans.ViewTypesBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.dao.mysql.NotificationsDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.SMTPDetailsPojo;
import com.heal.controlcenter.util.*;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class PutEmailConfigurationsBL implements BusinessLogic<SMTPDetailsPojo, UtilityBean<SMTPDetailsPojo>, Object> {

    @Autowired
    AccountsDao accountDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    NotificationsDao notificationsDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    DateTimeUtil dateTimeUtil;
    @Autowired
    AECSBouncyCastleUtil aecsBouncyCastleUtil;

    @Override
    public UtilityBean<SMTPDetailsPojo> clientValidation(SMTPDetailsPojo smtpDetailsPojo, String... requestParams) throws ClientException {
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

        Map<String, String> error = smtpDetailsPojo.validate();
        if (!error.isEmpty()) {
            String err = error.toString();
            log.error(err);
            throw new ClientException(err);
        }

        return UtilityBean.<SMTPDetailsPojo>builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .pojoObject(smtpDetailsPojo)
                .build();
    }

    @Override
    public UtilityBean<SMTPDetailsPojo> serverValidation(UtilityBean<SMTPDetailsPojo> utilityBean) throws ServerException {

        AccountBean account = accountDao.getAccountByIdentifier(utilityBean.getAccountIdentifier());
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        utilityBean.setAccount(account);

        return utilityBean;
    }

    @Override
    public Object process(UtilityBean<SMTPDetailsPojo> smtpUtilityBean) throws DataProcessingException {
        Date time = dateTimeUtil.getCurrentTimestampInGMT();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_TIME);
        String createdTime = dateFormat.format(time);
        SMTPDetailsPojo smtpDetails = smtpUtilityBean.getPojoObject();
        AccountBean account = smtpUtilityBean.getAccount();
        String plainTxt = "";

        SMTPDetailsBean smtpDetailsBeanExists = notificationsDao.getSMTPDetails(account.getId());
        if (smtpDetailsBeanExists == null) {
            log.error("Email details not found for accountId [{}].", account.getId());
            throw new DataProcessingException("Error occurred, Email details not found.");
        }

        ViewTypesBean securityType;
        try {
            securityType = masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Constants.SMTP_PROTOCOLS, smtpDetails.getSecurity());
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }
        if (securityType == null) {
            log.error("Security type details not found for security type [{}].", smtpDetails.getSecurity());
            throw new DataProcessingException("Error occurred, security type details not found.");
        }

        if (smtpDetails.getPassword() == null || smtpDetails.getPassword().trim().isEmpty()) {
            smtpDetails.setPassword("");

        } else {
            try {
                plainTxt = aecsBouncyCastleUtil.decrypt(smtpDetails.getPassword());
                if (plainTxt.isEmpty()) {
                    String err = "Error occurred, Password is not encrypted properly.";
                    log.error(err);
                    throw new DataProcessingException(err);
                }
            } catch (InvalidCipherTextException | DataLengthException e) {
                log.error("Exception encountered while decrypting the password. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
                throw new DataProcessingException("Error occurred while decrypting the password.");
            }
        }

        try {
            smtpDetails.setPassword(commonUtils.encryptInBCEC(plainTxt));
        } catch (Exception e) {
            log.error("Exception encountered while encrypting the password. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
            throw new DataProcessingException("Error occurred while encrypting the password from the database.");
        }

        SMTPDetailsBean smtpDetailsBean = SMTPDetailsBean.builder()
                .accountId(account.getId())
                .address(smtpDetails.getAddress())
                .port(smtpDetails.getPort())
                .lastModifiedBy(smtpUtilityBean.getUserId())
                .username(smtpDetails.getUsername())
                .password(smtpDetails.getPassword())
                .securityId(securityType.getSubTypeId())
                .createdTime(createdTime)
                .updatedTime(createdTime)
                .status(1)
                .fromRecipient(smtpDetails.getFromRecipient())
                .build();

        try {
            notificationsDao.updateSMTPDetails(smtpDetailsBean);
        } catch (Exception e) {
            throw new DataProcessingException("Error occurred, couldn't update SMTP details.");
        }
        return null;
    }
}

package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.SMTPDetailsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.dao.mysql.NotificationsDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.SMTPDetailsPojo;
import com.heal.controlcenter.util.AECSBouncyCastleUtil;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetEmailConfigurationsBL implements BusinessLogic<Object, Integer, SMTPDetailsPojo> {

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    AccountsDao accountDao;
    @Autowired
    NotificationsDao notificationsDao;
    @Autowired
    MasterDataDao masterDataDao;
    @Autowired
    AECSBouncyCastleUtil aecsBouncyCastleUtil;

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
    public SMTPDetailsPojo process(Integer accountId) throws DataProcessingException {
        String security = "";
        SMTPDetailsBean smtpDetailsBean = notificationsDao.getSMTPDetails(accountId);

        if (smtpDetailsBean == null) {
            log.error("SMTP details not found for accountId [{}].", accountId);
            return null;
        }

        String encryptedString = smtpDetailsBean.getPassword();
        smtpDetailsBean.setPassword(decryptBCECAndEncryptAECS(encryptedString));
        if (smtpDetailsBean.getSecurityId() > 0) {
            security = masterDataDao.getMstSubTypeBySubTypeId(smtpDetailsBean.getSecurityId()).getSubTypeName();
        }

        return SMTPDetailsPojo.builder()
                .id(smtpDetailsBean.getId())
                .address(smtpDetailsBean.getAddress())
                .port(smtpDetailsBean.getPort())
                .username(smtpDetailsBean.getUsername())
                .password(smtpDetailsBean.getPassword())
                .fromRecipient(smtpDetailsBean.getFromRecipient())
                .security(security)
                .build();
    }

    private String decryptBCECAndEncryptAECS(String input) throws DataProcessingException {
        String plainTxt;
        try {
            plainTxt = commonUtils.decryptInBCEC(input);
        } catch (Exception e) {
            log.error("Exception encountered while decrypting the password. Details: {}", e.getMessage());
            throw new DataProcessingException("Error occurred while decrypting the password from the database.");
        }
        try {
            return aecsBouncyCastleUtil.encrypt(plainTxt);
        } catch (Exception e) {
            log.error("Exception encountered while encrypting the password. Details: {}", e.getMessage());
            throw new DataProcessingException("Error occurred while encrypting the password from the database.");
        }
    }
}

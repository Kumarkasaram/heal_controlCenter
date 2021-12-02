package com.heal.controlcenter.util;

import com.appnomic.appsone.keycloak.KeyCloakSessionValidator;
import com.appnomic.appsone.model.JWTData;
import com.appnomic.appsone.util.KeyCloakConnectionSpec;
import com.appnomic.appsone.util.KeyCloakUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.pojo.KeyCloakUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class KeyCloakAuthService {

    private static KeyCloakSessionValidator keyCloakSessionValidator = null;
    private static KeyCloakUtility keyCloakUtility = null;
    private static final ObjectMapper objectMapper = CommonUtils.getObjectMapperWithHtmlEncoder();

    @Value("${ds.keycloak.ip}")
    public String keycloakIp;
    @Value("${ds.keycloak.port}")
    public String keycloakPort;
    @Value("${ds.keycloak.user}")
    public String username;
    @Value("${ds.keycloak.pwd}")
    public String encryptedPassword;

    public void init() {
        try {
            log.debug("Inside Session Validator method");
            KeyCloakConnectionSpec keyCloakConnectionSpec = this.getSpec();
            keyCloakSessionValidator = new KeyCloakSessionValidator(keyCloakConnectionSpec);
            keyCloakUtility = new KeyCloakUtility(keyCloakConnectionSpec);
        } catch (Exception e) {
            log.error("Error occurred while getting key cloak connection spec.", e);
        }
    }

    public KeyCloakConnectionSpec getSpec() {
        if (keycloakIp == null || keycloakPort == null || username == null || encryptedPassword == null) {
            log.error("Missing keycloak specific configuration in conf file.");
            System.exit(-1);
        }

        String decryptedPwd = CommonUtils.getDecryptedData(encryptedPassword);

        log.debug("Inside Session Validator method");
        KeyCloakConnectionSpec keyCloakConnectionSpec = new KeyCloakConnectionSpec()
                .setKeyCloakIP(keycloakIp)
                .setKeyCloakPort(keycloakPort)
                .setKeyCloakUsername(username)
                .setKeyCloakPassword(decryptedPwd)
                .createKeyCloakConnectionSpec();

        log.debug("KeycloakSpec : {}" , keyCloakConnectionSpec);
        return keyCloakConnectionSpec;
    }

    public boolean isValidKey(String appToken) {
        try {
            if(keyCloakSessionValidator == null) {
                this.init();
            }
            boolean status = keyCloakSessionValidator.validateJwsToken(appToken);
            log.trace("Status of the token is [{}]" , status);
            return status;

        } catch (Exception e) {
            log.error("Invalid token. Reason: {}" , e.getMessage(), e);
            return false;
        }
    }

    public JWTData extractUserDetails(String appToken) throws ControlCenterException {
        if (keyCloakUtility == null)
            this.init();

        if(keyCloakUtility == null) {
            throw new ControlCenterException("Unable to initialize the KeyCloak server. Kindly look into the appsone-cc logs.");
        }
        log.trace("Invoked method: extractUserDetails");
        JWTData jwtData = keyCloakUtility.extractUsername(appToken);

        if(jwtData == null) {
            throw new ControlCenterException("Unable to get the username from token. Kindly look into the appsone-cc logs.");
        }

        return jwtData;
    }

    public static KeyCloakUserDetails getKeyCloakUserDetails(String userData)  {
        KeyCloakUserDetails keyCloakUserDetails = null;
        try {
            keyCloakUserDetails = objectMapper.readValue(userData, KeyCloakUserDetails.class);
        } catch (IOException e) {
            log.error("Error occurred while reading user details from keycloak service {}",e.getMessage());
            log.debug("trace: ", e);
        }

        return keyCloakUserDetails;
    }

    public KeyCloakUserDetails getKeycloakUserDataFromId(String userIdentifier)   {
        KeyCloakUserDetails userData=null;
        if (keyCloakUtility == null) this.init();
        try {
            String userDetailsString = keyCloakUtility.fetchUserDetails(userIdentifier);
            userData = getKeyCloakUserDetails(userDetailsString);
        }   catch (Exception e) {
            log.error("Error occurred while fetching username from keycloak: ",e);
        }
        return userData;
    }



}

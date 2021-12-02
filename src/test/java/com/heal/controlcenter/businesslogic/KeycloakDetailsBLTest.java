package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.util.JsonFileParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakDetailsBLTest {

    @Mock
    JsonFileParser confProperties;
    @Mock
    UserDao userDataDao;

    @InjectMocks
    KeycloakDetailsBL keycloakDetailsBL;

    Map<String, Object> data = new HashMap<>();

    @BeforeEach
    void setUp() {
        data.put("realm","master");
        data.put("url","https://192.168.13.44:8443/auth");
        data.put("ssl-required","external");
        data.put("clientId","appsone-sso");
        data.put("credentials", new LinkedHashMap<String, Object>().put("secret", "c47a9096-bc2e-4952-9947-d0206d505f1f"));
        data.put("confidential-port", 0);
        ReflectionTestUtils.setField(keycloakDetailsBL, "setupType", "keycloak");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void process() throws DataProcessingException {
        when(confProperties.loadKeycloakSSOConfig()).thenReturn(data);
        when(userDataDao.getHealSetup()).thenReturn("Keycloak");
        Map<String, Object> mockFileData = keycloakDetailsBL.process("Keycloak settings");
        verify(confProperties, times(1)).loadKeycloakSSOConfig();
        verify(userDataDao, times(1)).getHealSetup();
        assertEquals(data, mockFileData);
    }

    @Test
    void getProcessDataForEmptySetup() throws DataProcessingException {
        when(confProperties.loadKeycloakSSOConfig()).thenReturn(data);
        when(userDataDao.getHealSetup()).thenReturn(null);
        Map<String, Object> mockFileData = keycloakDetailsBL.process("Keycloak settings");
        verify(confProperties, times(1)).loadKeycloakSSOConfig();
        verify(userDataDao, times(1)).getHealSetup();
        assertEquals(data, mockFileData);
    }
}
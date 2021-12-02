package com.heal.controlcenter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.businesslogic.KeycloakDetailsBL;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.util.JsonFileParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KeycloakSettingsController.class)
class KeycloakSettingsControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private KeycloakDetailsBL keycloakDetailsBL;

    @MockBean
    JsonFileParser headersParser;

    LinkedHashMap<String, Object> keyCloakSettingsJson;

    @BeforeEach
    void setUp() {
        keyCloakSettingsJson = new LinkedHashMap<>();
        keyCloakSettingsJson.put("realm","master");
        keyCloakSettingsJson.put("url","https://192.168.13.44:8443/auth");
        keyCloakSettingsJson.put("ssl-required","external");
        keyCloakSettingsJson.put("clientId","appsone-sso");
        LinkedHashMap<String, String> value = new LinkedHashMap<>();
        value.put("secret", "c47a9096-bc2e-4952-9947-d0206d505f1f");
        keyCloakSettingsJson.put("credentials", value);
        keyCloakSettingsJson.put("confidential-port", 0);
        keyCloakSettingsJson.put("isActiveDirectory", false);
    }

    @AfterEach
    void tearDown() {
        keyCloakSettingsJson.clear();
    }

    @Test
    void testGetKeyCloakSettings() throws Exception {
        Mockito.when(keycloakDetailsBL.process(anyString())).thenReturn(keyCloakSettingsJson);
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/keycloak-settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mockHttpServletResponse.getContentAsString()).contains(objectMapper.writeValueAsString(keyCloakSettingsJson));
    }

    @Test
    void testGetKeyCloakSettings_WhenError() throws Exception {
        Mockito.when(keycloakDetailsBL.process(anyString())).thenThrow(DataProcessingException.class);
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/keycloak-settings")
                        .header("authorization", "check2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("BAD_REQUEST")))
                .andExpect(jsonPath("$.data", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$.data", Matchers.hasKey("status")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("type")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("path")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("error")))
                .andReturn()
                .getResponse();
    }
}
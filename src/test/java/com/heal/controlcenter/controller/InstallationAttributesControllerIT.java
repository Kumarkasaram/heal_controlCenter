package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.InstallationAttributeBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.InstallationAttributeBL;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Sourav Suman - 17-10-2021
 */

@WebMvcTest(InstallationAttributesController.class)
class InstallationAttributesControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    InstallationAttributeBL installationAttributeBL;

    @MockBean
    JsonFileParser headersParser;

    List<InstallationAttributeBean> listOfInstallationAttributes = null;
    InstallationAttributeBean installationAttributeBean = null;
    UtilityBean<String> mockUtilityBean = null;

    @BeforeEach
    void setUp() {
        installationAttributeBean = new InstallationAttributeBean();
        listOfInstallationAttributes = new ArrayList<>();
        mockUtilityBean = UtilityBean.<String>builder()
                .pojoObject("mockUserId")
                .accountIdentifier("mockUserId")
                .authToken("mockUserId")
                .build();
        installationAttributeBean.setName("DefaultCredentials");
        installationAttributeBean.setValue("LN0W7lTZIO9846EQcz3Vrg==");
        listOfInstallationAttributes.add(installationAttributeBean);
    }

    @AfterEach
    void tearDown() {
        installationAttributeBean = null;
        listOfInstallationAttributes = null;
    }

    @Test
    void getInstallationAttributes() throws Exception {
        Mockito.when(installationAttributeBL.clientValidation(null, "check2")).thenReturn(mockUtilityBean);
        Mockito.when(installationAttributeBL.process(anyString())).thenReturn(listOfInstallationAttributes);
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/installation-attributes")
                        .header("authorization", "check2"))
                .andExpect(status().isOk())
                .andExpect(header().string("authorization", "check2"))
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Installation details fetching successfully")))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andReturn()
                .getResponse();
        assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.OK.value());
    }

    @Test
    void getInstallationAttributes_WhenError() throws Exception {
        Mockito.when(installationAttributeBL.clientValidation(null, "check2")).thenReturn(mockUtilityBean);
        Mockito.when(installationAttributeBL.process(anyString())).thenThrow(DataProcessingException.class);
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/installation-attributes")
                        .header("authorization", "check2"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("authorization", "check2"))
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
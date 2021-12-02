package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.IdBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.UserRoleBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.util.JsonFileParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author Sourav Suman - 18-10-2021
 */

@WebMvcTest(RolesAndProfileController.class)
class RolesAndProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserRoleBL userRoleBL;
    @MockBean
    JsonFileParser headersParser;

    UtilityBean<String> mockutilityBean = null;
    IdBean idBean = null;
    List<IdBean> mockList = null;

    @BeforeEach
    void setUp() {
        idBean = new IdBean();
        mockList = new ArrayList<>();
        mockutilityBean = UtilityBean.<String>builder()
                .pojoObject("mockUserId")
                .accountIdentifier("mockUserId")
                .authToken("mockUserId")
                .build();
        idBean.setId(2);
        idBean.setName("Test");
        idBean.setIdentifier("identifier1");
        mockList.add(idBean);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserRoles() throws Exception {
        Mockito.when(userRoleBL.clientValidation(any(), any())).thenReturn(mockutilityBean);
        Mockito.when(userRoleBL.process(anyString())).thenReturn(mockList);
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/roles")
                        .header("Authorization", "check2"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "check2"))
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", Matchers.equalTo("Test")))
                .andReturn()
                .getResponse();
    }

    @Test
    void getUserRoles_WhenClientError() throws Exception {
        doThrow(ClientException.class).when(userRoleBL).clientValidation(any(), any());
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/roles")
                        .header("Authorization", "check2"))
                .andExpect(status().is4xxClientError())
                .andExpect(header().string("Authorization", "check2"))
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

    @Test
    void getUserRoles_When500Error() throws Exception {
        given(userRoleBL.clientValidation(any(), anyString())).willAnswer( exc -> { throw new Exception("Test exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/roles")
                        .header("Authorization", "check2"))
                .andExpect(status().is5xxServerError())
                .andExpect(header().string("Authorization", "check2"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("INTERNAL_SERVER_ERROR")))
                .andExpect(jsonPath("$.data", Matchers.aMapWithSize(6)))
                .andExpect(jsonPath("$.data", Matchers.hasKey("status")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("type")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("path")))
                .andExpect(jsonPath("$.data", Matchers.hasKey("error")))
                .andReturn()
                .getResponse();
    }
}
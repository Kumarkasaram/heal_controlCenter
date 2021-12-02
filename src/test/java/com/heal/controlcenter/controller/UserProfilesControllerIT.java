package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UserProfileBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.UserProfilesBL;
import com.heal.controlcenter.exception.ClientException;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Sourav Suman - 20-10-2021
 */

@WebMvcTest(UserProfilesController.class)
class UserProfilesControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserProfilesBL userProfilesBL;
    @MockBean
    JsonFileParser headersParser;

    UtilityBean<String> mockUtilityBean = null;
    UserProfileBean userProfileBean = null;
    List<UserProfileBean> mockList = null;

    @BeforeEach
    void setUp() {
        userProfileBean = new UserProfileBean() {{
            setUserProfileId(1);
            setRole("role1");
            setUserProfileName("user profile name");
            setAccessibleFeatures(new HashSet<String>() {{
                add("Setup Topology");
            }});
        }};
        mockList = new ArrayList<UserProfileBean>() {{
            add(userProfileBean);
        }};
        mockUtilityBean = UtilityBean.<String>builder()
                .pojoObject("mockUserId")
                .accountIdentifier("mockUserId")
                .authToken("mockUserId")
                .build();
    }

    @AfterEach
    void tearDown() {
        mockUtilityBean = null;
        mockList = null;
        userProfileBean = null;
    }

    @Test
    void getUserProfiles() throws Exception {
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("Authorization", "check2");
        }});
        Mockito.when(userProfilesBL.clientValidation(any(), any())).thenReturn(mockUtilityBean);
        Mockito.when(userProfilesBL.process(anyString())).thenReturn(mockList);
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/user-profiles")
                        .header("Authorization", "check2"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "check2"))
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].role", Matchers.equalTo("role1")))
                .andExpect(jsonPath("$.data[0].accessibleFeatures", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Timezones fetched successfully")))
                .andReturn().getResponse();
    }

    @Test
    void getUserProfiles_WhenClientError() throws Exception {
        doThrow(ClientException.class).when(userProfilesBL).clientValidation(any(), any());
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/user-profiles")
                        .header("Authorization", "check2"))
                .andExpect(status().is4xxClientError())
                .andExpect(header().string("Authorization", "check2"))
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
    void getUserProfiles_DataProcessingException() throws Exception {
        doThrow(DataProcessingException.class).when(userProfilesBL).process(any());
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/user-profiles")
                        .header("Authorization", "check2"))
                .andExpect(status().is4xxClientError())
                .andExpect(header().string("Authorization", "check2"))
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
    void getUserProfiles_When500Error() throws Exception {
        given(userProfilesBL.clientValidation(any(), anyString())).willAnswer(exc -> {
            throw new Exception();
        });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/user-profiles")
                        .header("Authorization", "check2"))
                .andExpect(status().is5xxServerError())
                .andExpect(header().string("Authorization", "check2"))
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
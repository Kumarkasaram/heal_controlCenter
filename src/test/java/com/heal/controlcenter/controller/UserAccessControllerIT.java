package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UserAccessibleActions;
import com.heal.controlcenter.beans.UserAttributesBean;
import com.heal.controlcenter.beans.UserDetailsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.UserAccessibleActionBL;
import com.heal.controlcenter.businesslogic.UserDetailsBL;
import com.heal.controlcenter.exception.ClientException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserAccessController.class)
class UserAccessControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    JsonFileParser headersParser;
    @MockBean
    UserAccessibleActionBL userAccessibleActionBL;
    @MockBean
    UserDetailsBL userDetailsBL;

    List<UserDetailsBean> userDetailsBeanList;
    UserDetailsBean userDetailsBean;

    @BeforeEach
    void setUp() {
        userDetailsBeanList = new ArrayList<>();
        userDetailsBean = new UserDetailsBean();
        userDetailsBean.setUserProfile("Profile1");
        userDetailsBean.setUserId("userId1");
        userDetailsBean.setStatus(2);
        userDetailsBean.setUpdatedBy("appsoneadmin");
        userDetailsBean.setUserName("Username");
        userDetailsBean.setUpdatedOn(242453600009L);
        userDetailsBean.setEmailNotification(2);
        userDetailsBean.setForensicNotification(0);
        userDetailsBean.setSmsNotification(1);
        userDetailsBeanList.add(userDetailsBean);
    }

    @AfterEach
    void tearDown() {
        userDetailsBeanList = null;
        userDetailsBean = null;
    }

    @Test
    void getUserAccessInformation() throws Exception {
        UserAccessibleActions userActions = new UserAccessibleActions() {{
            setIsActiveDirectory(0);
            setProfile("Super Admin");
            setProfileId(1);
            setRole("Super Admin");
            setRoleId(1);
            setAllowedActions(new ArrayList<String>() {{
                add("action_1");
                add("action_2");
            }});
        }};

        when(userAccessibleActionBL.clientValidation(null, "raw string")).thenReturn(UtilityBean.<String>builder().build());
        when(userAccessibleActionBL.serverValidation(any())).thenReturn(new UserAttributesBean());
        when(userAccessibleActionBL.process(any())).thenReturn(userActions);

        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users/access-info")
                        .header("Authorization", "raw string"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getUserAccessInformation_WhenClientError() throws Exception {
        doThrow(ClientException.class).when(userAccessibleActionBL).clientValidation(any(), any());
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users/access-info")
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
    void getUserAccessInformation_When500Error() throws Exception {
        given(userAccessibleActionBL.clientValidation(any(), anyString())).willAnswer( exc -> { throw new Exception("Test exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users/access-info")
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

    @Test
    void getUsers() throws Exception {
        when(userDetailsBL.clientValidation(null, "check2")).thenReturn(UtilityBean.<String>builder().build());
        when(userDetailsBL.process(any())).thenReturn(userDetailsBeanList);
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users")
                        .header("Authorization", "check2"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "check2"))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Users detail fetch successfully")))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andReturn().getResponse();
        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getUsers_WhenClientError() throws Exception {
        doThrow(ClientException.class).when(userDetailsBL).clientValidation(any(), any());
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users")
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
    void getUsers_When500Error() throws Exception {
        given(userDetailsBL.clientValidation(any(), anyString())).willAnswer( exc -> { throw new Exception("Test exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/users")
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
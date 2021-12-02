package com.heal.controlcenter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.beans.TimezoneBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.TimeZoneBL;
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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeZoneController.class)
class TimeZoneControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    TimeZoneBL timeZoneBL;
    @MockBean
    JsonFileParser headersParser;
    @Autowired
    ObjectMapper objectMapper;

    List<TimezoneBean> listOfTimeZones = null;
    TimezoneBean timezoneBean = null;
    UtilityBean<String> mockutilityBean = null;

    @BeforeEach
    void setUp() {
        mockutilityBean = UtilityBean.<String>builder()
                .pojoObject("mockUserId")
                .accountIdentifier("mockUserId")
                .authToken("mockUserId")
                .build();
        timezoneBean = new TimezoneBean();
        timezoneBean.setAccountId(124667);
        timezoneBean.setOffset(788999772);
        timezoneBean.setUserDetailsId("UserId 1");
        timezoneBean.setStatus(0);
        listOfTimeZones = new ArrayList<>();
        listOfTimeZones.add(timezoneBean);
    }

    @AfterEach
    void tearDown() {
        listOfTimeZones = null;
        timezoneBean = null;
    }

    @Test
    void getAllTimezones() throws Exception {
        Mockito.when(timeZoneBL.clientValidation(null, "check2")).thenReturn(mockutilityBean);
        Mockito.when(timeZoneBL.process(anyString())).thenReturn(listOfTimeZones);
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
            set("authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/timezones")
                        .header("authorization", "check2"))
                .andExpect(status().isOk())
                .andExpect(header().string("authorization", "check2"))
                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].accountId", Matchers.equalTo(124667)))
                .andReturn()
                .getResponse();
        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
    }

    @Test
    void getUserRoles_When400Error_ClientException() throws Exception {
        given(timeZoneBL.clientValidation(any(), anyString())).willAnswer( exc -> { throw new ClientException("Test client exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/timezones")
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
    void getUserRoles_When400Error_DataProcessingException() throws Exception {
        given(timeZoneBL.process(anyString())).willAnswer( exc -> { throw new DataProcessingException("Test data processing exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/timezones")
                        .header("Authorization", "check2"))
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
        given(timeZoneBL.process(anyString())).willAnswer( exc -> { throw new Exception("Test data exception"); });
        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
            set("Authorization", "check2");
        }});
        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/timezones")
                        .header("Authorization", "check2"))
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
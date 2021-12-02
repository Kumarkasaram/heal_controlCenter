package com.heal.controlcenter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heal.controlcenter.beans.AgentTypePojo;
import com.heal.controlcenter.beans.ComponentAttributesMapping;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.GetAgentTypeAtAccLvlBL;
import com.heal.controlcenter.businesslogic.GetComponentAttributesBL;
import com.heal.controlcenter.businesslogic.GetComponentAttributesBLTest;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.util.JsonFileParser;

@WebMvcTest(ApplicationsController.class)
public class ApplicationControllerIT {
	  @Autowired
	    private MockMvc mockMvc;
	    @MockBean
	    GetAgentTypeAtAccLvlBL getAgentTypeAtAccLvlBL;
	    @MockBean
	    GetComponentAttributesBL getComponentAttributesBL;
	    @MockBean
	    JsonFileParser headersParser;
	    @Autowired
	    ObjectMapper objectMapper;

	    List<AgentTypePojo> agentTypePojoList= null;
	    List<ComponentAttributesMapping> componentAttributesMappingList = null;
	    UtilityBean<Object> mockutilityBean = null;

	    @BeforeEach
	    void setUp() {
	        mockutilityBean = UtilityBean.<Object>builder()
	                .pojoObject("mockUserId")
	                .accountIdentifier("mockUserId")
	                .authToken("mockUserId")
	                .build();
	         
	        agentTypePojoList = new ArrayList<>();
	        agentTypePojoList.add(AgentTypePojo.builder() 
	    	        .id(12)
	    	        .name("test").build());
	        componentAttributesMappingList = new ArrayList<>();
	        componentAttributesMappingList.add(ComponentAttributesMapping.builder()
	    	        .id(13)
	    	        .name("test").build());
	    }

	    @AfterEach
	    void tearDown() {
	    	agentTypePojoList = null;
	    	componentAttributesMappingList=null;
	    }

	    @Test
	    void getAgentTypesAtAccLvl() throws Exception {
	        Mockito.when(getAgentTypeAtAccLvlBL.clientValidation(Mockito.anyString())).thenReturn(mockutilityBean);
	        Mockito.when(getAgentTypeAtAccLvlBL.process(Mockito.anyInt())).thenReturn(agentTypePojoList);
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
	            set("authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/agent-types")
	                        .header("authorization", "check2"))
	                .andExpect(status().isOk())
	                .andExpect(header().string("authorization", "check2"))
	                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
	                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
	                .andExpect(jsonPath("$.data[0].id", Matchers.equalTo(12)))
	                .andReturn()
	                .getResponse();
	        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
	        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }

	    @Test
	    void getAgentTypesAtAccLvlException() throws Exception {
	        given(getAgentTypeAtAccLvlBL.clientValidation(Mockito.anyString())).willAnswer( exc -> { throw new ClientException("Test client exception"); });
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
	            set("Authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/test/agent-types")
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
	    void getComponentAttributes() throws Exception {
	    	  Mockito.when(getComponentAttributesBL.clientValidation(Mockito.anyString())).thenReturn(mockutilityBean);
		        Mockito.when(getComponentAttributesBL.process(Mockito.anyInt())).thenReturn(componentAttributesMappingList);
		        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
		            set("authorization", "check2");
		        }});
		        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/ytrvvv/component-attributes")
		                        .header("authorization", "check2"))
		                .andExpect(status().isOk())
		                .andExpect(header().string("authorization", "check2"))
		                .andExpect(jsonPath("$.responseStatus", Matchers.equalTo("OK")))
		                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
		                .andExpect(jsonPath("$.data[0].id", Matchers.equalTo(12)))
		                .andReturn()
		                .getResponse();
		        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }

	    @Test
	    void getComponentAttributes_When500Error() throws Exception {
	        given(getComponentAttributesBL.process(Mockito.anyInt())).willAnswer( exc -> { throw new Exception("Test data exception"); });
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
	            set("Authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/component-attributes")
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

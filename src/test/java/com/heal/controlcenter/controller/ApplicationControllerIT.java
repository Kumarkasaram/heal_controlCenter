package com.heal.controlcenter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.heal.controlcenter.beans.*;
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
import com.heal.controlcenter.businesslogic.GetAgentTypeAtAccLvlBL;
import com.heal.controlcenter.businesslogic.GetAvailabilityCategoriesBL;
import com.heal.controlcenter.businesslogic.GetComponentAttributesBL;
import com.heal.controlcenter.businesslogic.GetComponentDetailsBL;
import com.heal.controlcenter.exception.ClientException;
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
	    GetComponentDetailsBL getComponentDetailsBL;
	    @MockBean
	    GetAvailabilityCategoriesBL getAvailabilityCategoriesBL;
	    @MockBean
	    JsonFileParser headersParser;
	    @Autowired
	    ObjectMapper objectMapper;

	    List<AgentTypePojo> agentTypePojoList= null;
	    List<ComponentAttributesMapping> componentAttributesMappingList = null;
	    UtilityBean<Object> mockutilityBean = null;
	    List<ComponentDetails> componentDetailsList =null;
	    List<GetCategory> getcategoryList  =null;

	    @BeforeEach
	    void setUp() {
	        mockutilityBean = UtilityBean.<Object>builder().pojoObject("mockUserId").accountIdentifier("mockUserId").authToken("mockUserId").build();
	         
	        agentTypePojoList = new ArrayList<>();
	        agentTypePojoList.add(AgentTypePojo.builder() .id(12).name("test").build());

			ComponentAttributesMapping componentAttributesMapping = new ComponentAttributesMapping() {{
				setId(13);
				setName("test");
			}};

	        componentAttributesMappingList = new ArrayList<>();
	        componentAttributesMappingList.add(componentAttributesMapping);
	        
	        //Mock CommonDetail
	        componentDetailsList = new ArrayList<>();
	        ComponentDetails componentDetails = new ComponentDetails();
	        componentDetailsList.add(ComponentDetails.builder().id(12).name("componentDetailTest").build());
	        
	        //mock getCtegoryList
	        getcategoryList = new ArrayList<>();
	        getcategoryList.add(GetCategory.builder().id(12).name("getcategoryTest").workLoad(1).build());
	    }

	    @AfterEach
	    void tearDown() {
	    	agentTypePojoList = null;
	    	componentAttributesMappingList=null;
	    	componentDetailsList =null;
	    	getcategoryList=null;
	    }

	    @Test
	    void getAgentTypesAtAccLvl() throws Exception {
	        Mockito.when(getAgentTypeAtAccLvlBL.clientValidation(Mockito.anyString())).thenReturn(mockutilityBean);
	        Mockito.when(getAgentTypeAtAccLvlBL.process(Mockito.anyInt())).thenReturn(agentTypePojoList);
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
	            set("authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/agent-types","identifier")
	                        .header("authorization", "check2"))
	                .andExpect(status().isOk())
	                .andExpect(header().string("authorization", "check2"))
	                .andExpect(jsonPath("$.[0].id").value(12))
	                .andReturn()
	                .getResponse();
	        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
	        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }

	    @Test
	    void getAgentTypesAtAccLvlException() throws Exception {
	        given(getAgentTypeAtAccLvlBL.process(Mockito.anyInt())).willAnswer( exc -> { throw new Exception("Test data exception"); });
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
	            set("Authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/agent-types","identifier2")
	                        .header("Authorization", "check2"))
					.andExpect(status().isInternalServerError())
					.andExpect(header().string("Authorization", "check2"))
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
		        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/component-attributes","identifier")
		                        .header("authorization", "check2"))
		                .andExpect(status().isOk())
		                .andExpect(header().string("authorization", "check2"))
						.andExpect(jsonPath("$.[0].id").value("13"))
						.andExpect(jsonPath("$.[0].name").value( "test"))
		                //.andExpect(jsonPath("$.data[0].id", Matchers.equalTo(12)))
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
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/component-attributes","identifier")
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
			assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
			assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }
	    
	    @Test
	    void getComponentDetails() throws Exception {
	    	  Mockito.when(getComponentDetailsBL.clientValidation(Mockito.anyString())).thenReturn(mockutilityBean);
		        Mockito.when(getComponentDetailsBL.process(Mockito.anyInt())).thenReturn(componentDetailsList);
		        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
		            set("authorization", "check2");
		        }});
		        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/component-details","identifier")
		                        .header("authorization", "check2"))
		                .andExpect(status().isOk())
		                .andExpect(header().string("authorization", "check2"))
		                .andExpect(jsonPath("$.[0].id").value(12))
		                .andReturn()
		                .getResponse();
		        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }
	    
	    @Test
	    void getComponentDetails_When500Error() throws Exception {
	        given(getComponentDetailsBL.process(Mockito.anyInt())).willAnswer( exc -> { throw new Exception("Test data exception"); });
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
	            set("Authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/component-details","identifier")
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
	    
	    @Test
	    void availabilityCategories() throws Exception {
	    	  Mockito.when(getAvailabilityCategoriesBL.clientValidation(Mockito.anyString())).thenReturn(mockutilityBean);
		        Mockito.when(getAvailabilityCategoriesBL.process(Mockito.anyInt())).thenReturn(getcategoryList);
		        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders() {{
		            set("authorization", "check2");
		        }});
		        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/availabilityCategories","identifier")
		                        .header("authorization", "check2"))
		                .andExpect(status().isOk())
		                .andExpect(header().string("authorization", "check2"))
		                .andExpect(jsonPath("$.[0].id").value(12))
		                .andReturn()
		                .getResponse();
		        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		        assertThat(mockHttpServletResponse.getHeader("authorization")).isEqualTo("check2");
	    }
	    
	    @Test
	    void availabilityCategories_When500Error() throws Exception {
	        given(getAvailabilityCategoriesBL.process(Mockito.anyInt())).willAnswer( exc -> { throw new Exception("Test data exception"); });
	        Mockito.when(headersParser.loadHeaderConfiguration()).thenReturn(new HttpHeaders(){{
	            set("Authorization", "check2");
	        }});
	        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(get("/accounts/{identifier}/availabilityCategories","identifier")
	                        .header("Authorization", "check2"))
	                .andExpect(header().string("Authorization", "check2"))
	                .andExpect(status().isInternalServerError())
	                .andExpect(jsonPath("$.data", Matchers.aMapWithSize(6)))
	                .andExpect(jsonPath("$.data", Matchers.hasKey("status")))
	                .andExpect(jsonPath("$.data", Matchers.hasKey("type")))
	                .andExpect(jsonPath("$.data", Matchers.hasKey("path")))
	                .andExpect(jsonPath("$.data", Matchers.hasKey("error")))
	                .andReturn()
	                .getResponse();
	    }
}

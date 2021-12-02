package com.heal.controlcenter.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.AgentTypePojo;
import com.heal.controlcenter.beans.ComponentAttributesMapping;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.beans.ViewComponentAttributesPojo;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ComponentDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;

@ExtendWith(MockitoExtension.class)
public class GetComponentAttributesBLTest  {
	@InjectMocks
	GetComponentAttributesBL getComponentAttributesBL;
	@Mock
	AccountsDao accountsDao;
	@Mock
	CommonUtils commonUtils;
	@Mock
	ComponentDao componentDao;
	List<ComponentAttributesMapping> componentAttributesMappingList = null;
	AccountBean accountBean =null;
	List<ViewComponentAttributesPojo> viewComponentAttributesPojoList =null;
	

	  @BeforeEach
	    void setUp() {
	        
	        accountBean = new AccountBean();
	        accountBean.setId(12);
	        accountBean.setIdentifier("identifier");
	        accountBean.setName("accountBean");
	        accountBean.setStatus(1);
	        accountBean.setUserIdDetails("userDetail");
	        
	        viewComponentAttributesPojoList = new ArrayList();
	        viewComponentAttributesPojoList.add(ViewComponentAttributesPojo.builder().attributeId(12)
	        		.attributeName("attributeName")
	        		.componentName("componentName")
	        		.componentId(12)
	        		.componentTypeName("componentTypeName").build());
	        
	        componentAttributesMappingList = new ArrayList<>();
	        componentAttributesMappingList.add(ComponentAttributesMapping.builder()
	    	        .id(13)
	    	        .name("test").build());
	        
	    }

	    @AfterEach
	    void tearDown() {
	    	componentAttributesMappingList = null;
	    	viewComponentAttributesPojoList =null;
	        accountBean = null;
	    }

	    @Test
	    void clientValidation_InvalidToken() throws ControlCenterException {
	        String expectedMessage = "ClientException : Invalid authorization token";
	        ClientException exception = assertThrows(ClientException.class, () ->
	        getComponentAttributesBL.clientValidation(null,"","tyr8-uijjf8sooijfjfkjkjskjjkje" ));
	        assertEquals(expectedMessage, exception.getMessage());
	    }

	    @Test
	    void clientValidation() throws ControlCenterException, ClientException {
	        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        UtilityBean<Object> auth = getComponentAttributesBL.clientValidation(null,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","identifier" );
	        assertEquals(auth.getAuthToken(), userId);
	    }

	    @Test
	    void clientValidation_WhenRequestIsNull() {
	        String expectedMessage = "ClientException : Invalid account identifier";
	        ClientException requestException = assertThrows(ClientException.class, () ->
	        getComponentAttributesBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje",""));
	        assertEquals(expectedMessage,requestException.getMessage());
	    }

	    @Test
	    void serverValidation() throws ServerException, ControlCenterException {
	    	 String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
		    Integer accountId = getComponentAttributesBL.serverValidation(UtilityBean.<Object>builder().pojoObject(any()).build());
	        assert(accountId.equals(12));
	    }

	    @Test
	    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
	        String expectedMessage = "ServerException : Account identifier is invalid";
	        when(accountsDao.getAccountDetailsForIdentifier(Mockito.anyString())).thenReturn(null);
	        Exception ex = assertThrows(ServerException.class, () -> getComponentAttributesBL.serverValidation(UtilityBean.<Object>builder().accountIdentifier("gggh").build()));
	        assertEquals(expectedMessage,ex.getMessage());
	    }
	    
	    
	    @Test
	    void process() throws Exception {
	        when(componentDao.getComponentAttributeDetails()).thenReturn(viewComponentAttributesPojoList);
	        List<ComponentAttributesMapping> ComponentAttributesMapping = getComponentAttributesBL.process(12);
	        assertEquals(ComponentAttributesMapping.size(), 1);
	        assertEquals(ComponentAttributesMapping.get(0).getId(),12);
	        assertEquals(ComponentAttributesMapping.get(0).getName(),"componentName");
	    }

	  /*  @Test
	    void process_when_user_list_is_empty() throws DataProcessingException, ControlCenterException {
	        when(masterDataDao.getMasterSubTypeDetailsList()).thenReturn(new ArrayList<MasterSubTypeBean>());
	        List<ComponentAttributesMapping> agentTypeList = getComponentAttributesBL.process(Mockito.anyInt());
	        assertEquals(agentTypeList.size(), 0);
	    }
*/
}

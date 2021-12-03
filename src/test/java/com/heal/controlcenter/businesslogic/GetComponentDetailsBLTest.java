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
import com.heal.controlcenter.beans.ComponentDetails;
import com.heal.controlcenter.beans.MasterComponentBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.beans.ViewComponentAttributesPojo;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.ComponentDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;

@ExtendWith(MockitoExtension.class)
public class GetComponentDetailsBLTest  {
	@InjectMocks
	GetComponentDetailsBL getComponentDetailsBL;
	@Mock
	AccountsDao accountsDao;
	@Mock
	MasterDataDao masterDataDao;
	@Mock
	CommonUtils commonUtils;
	AccountBean accountBean =null;
	List<MasterComponentBean> componentMasterDataList =null;
	

	  @BeforeEach
	    void setUp() {
	        
	        accountBean = new AccountBean();
	        accountBean.setId(12);
	        accountBean.setIdentifier("identifier");
	        accountBean.setName("accountBean");
	        accountBean.setStatus(1);
	        accountBean.setUserIdDetails("userDetail");
	        
	        componentMasterDataList = new ArrayList<>();
	        componentMasterDataList.add(MasterComponentBean.builder().id(1).name("componentDetailTest").accountId(12).componentTypeName("componentName").componentTypeId(13).build());
	        
	    }

	    @AfterEach
	    void tearDown() {
	        accountBean = null;
	        componentMasterDataList =null;
	    }

	    @Test
	    void clientValidation_InvalidToken() throws ControlCenterException {
	        String expectedMessage = "ClientException : Invalid authorization token";
	        ClientException exception = assertThrows(ClientException.class, () ->
	        getComponentDetailsBL.clientValidation(null,"","tyr8-uijjf8sooijfjfkjkjskjjkje" ));
	        assertEquals(expectedMessage, exception.getMessage());
	    }

	    @Test
	    void clientValidation() throws ControlCenterException, ClientException {
	        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        UtilityBean<Object> auth = getComponentDetailsBL.clientValidation(null,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","identifier" );
	        assertEquals(auth.getAuthToken(), userId);
	    }

	    @Test
	    void clientValidation_WhenRequestIsNull() {
	        String expectedMessage = "ClientException : Invalid account identifier";
	        ClientException requestException = assertThrows(ClientException.class, () ->
	        getComponentDetailsBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje",""));
	        assertEquals(expectedMessage,requestException.getMessage());
	    }

	    @Test
	    void serverValidation() throws ServerException, ControlCenterException {
	    	 String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
		    Integer accountId = getComponentDetailsBL.serverValidation(UtilityBean.<Object>builder().pojoObject(any()).build());
	        assert(accountId.equals(12));
	    }

	    @Test
	    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
	        String expectedMessage = "ServerException : Account identifier is invalid";
	        when(accountsDao.getAccountDetailsForIdentifier(Mockito.anyString())).thenReturn(null);
	        Exception ex = assertThrows(ServerException.class, () -> getComponentDetailsBL.serverValidation(UtilityBean.<Object>builder().accountIdentifier("gggh").build()));
	        assertEquals(expectedMessage,ex.getMessage());
	    }
	    
	    
	    @Test
	    void process() throws Exception {
	        when(masterDataDao.getComponentMasterDataForAccountId(Mockito.anyInt())).thenReturn(componentMasterDataList);
	        List<ComponentDetails> componentDetailsList = getComponentDetailsBL.process(12);
	        assertEquals(componentDetailsList.size(), 1);
	        assertEquals(componentDetailsList.get(0).getId(),1);
	        assertEquals(componentDetailsList.get(0).getName(),"componentDetailTest");
	    }


}

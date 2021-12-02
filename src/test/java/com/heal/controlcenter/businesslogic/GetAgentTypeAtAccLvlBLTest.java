package com.heal.controlcenter.businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.heal.controlcenter.beans.MasterSubTypeBean;
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
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;

@ExtendWith(MockitoExtension.class)
public class GetAgentTypeAtAccLvlBLTest
{
	@InjectMocks
	GetAgentTypeAtAccLvlBL GetAgentTypeAtAccLvlBL;
	@Mock
	AccountsDao accountsDao;
	@Mock
	CommonUtils commonUtils;
	@Mock
	MasterDataDao masterDataDao;
	
	AccountBean accountBean =null;
	List<MasterSubTypeBean> masterSubTypeBeans =null;
	
	  @BeforeEach
	    void setUp() {
	        
	        accountBean = new AccountBean();
	        accountBean.setId(12);
	        accountBean.setIdentifier("identifier");
	        accountBean.setName("accountBean");
	        accountBean.setStatus(1);
	        accountBean.setUserIdDetails("userDetail");

		  masterSubTypeBeans = new ArrayList<>();
	        MasterSubTypeBean masterSubTypeBean = new MasterSubTypeBean();
	        masterSubTypeBean.setAccountId(12);
	        masterSubTypeBean.setId(1);
	        masterSubTypeBean.setName("test");
	        masterSubTypeBean.setMstTypeId(1);
	        masterSubTypeBeans.add(masterSubTypeBean);
	        
	    }

	    @AfterEach
	    void tearDown() {
	    	masterSubTypeBeans = null;
	        accountBean = null;
	    }

	    @Test
	    void clientValidation_InvalidToken() throws ControlCenterException {
	        String expectedMessage = "ClientException : Invalid account identifier";
	       // when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(any());
	        ClientException exception = assertThrows(ClientException.class, () ->
	        GetAgentTypeAtAccLvlBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje","" ));
	        assertEquals(expectedMessage, exception.getMessage());
	    }

	    @Test
	    void clientValidation() throws ControlCenterException, ClientException {
	        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	       // when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(userId);
	        UtilityBean<Object> auth = GetAgentTypeAtAccLvlBL.clientValidation(null,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","test" );
	        assertEquals(auth.getAuthToken(), userId);
	    }

	    @Test
	    void clientValidation_WhenRequestIsNull() {
	        String expectedMessage = "ClientException : Invalid authorization token";
	        ClientException requestException = assertThrows(ClientException.class, () ->
	        GetAgentTypeAtAccLvlBL.clientValidation(null,""),"tyr8-uijjf8sooijfjfkjkjskjjkje");
	        assertEquals(expectedMessage,requestException.getMessage());
	    }

	    @Test
	    void serverValidation() throws ServerException, ControlCenterException {
	    	 String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
		  //  when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(userId);
		    Integer accountId = GetAgentTypeAtAccLvlBL.serverValidation(UtilityBean.<Object>builder().pojoObject(any()).build());
	        assert(accountId.equals(12));
	    }

	    @Test
	    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
	        String expectedMessage = "ServerException : Invalid Account Identifier";
	        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(null);
	        Exception ex = assertThrows(ServerException.class, () -> GetAgentTypeAtAccLvlBL.serverValidation(UtilityBean.<Object>builder().pojoObject(any()).build()));
	        assertEquals(expectedMessage,ex.getMessage());
	    }
	    
	    
	    @Test
	    void process() throws Exception {
	        when(masterDataDao.getMasterSubTypeDetailsList()).thenReturn(masterSubTypeBeans);
	        List<AgentTypePojo> agentTypeList = GetAgentTypeAtAccLvlBL.process(12);
	        assertEquals(agentTypeList.size(), 1);
	        assertEquals(agentTypeList.get(0).getId(),1);
	        assertEquals(agentTypeList.get(0).getName(),"test");
	    }

	    @Test
	    void process_when_user_list_is_empty() throws DataProcessingException, ControlCenterException {
	        when(masterDataDao.getMasterSubTypeDetailsList()).thenReturn(masterSubTypeBeans);
	        List<AgentTypePojo> agentTypeList = GetAgentTypeAtAccLvlBL.process(12);
	        assertEquals(agentTypeList.size(), 1);
	    }

}

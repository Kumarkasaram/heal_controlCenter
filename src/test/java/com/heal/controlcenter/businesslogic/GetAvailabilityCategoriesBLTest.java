package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.CategoryDetailBean;
import com.heal.controlcenter.beans.GetCategory;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.CategoryDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAvailabilityCategoriesBLTest  {
	@InjectMocks
	GetAvailabilityCategoriesBL  getAvailabilityCategoriesBL;
	@Mock
	AccountsDao accountsDao;
	@Mock
	CategoryDao categoryDao;
	@Mock
	CommonUtils commonUtils;
	
	List<CategoryDetailBean> categoryDetailList = null;
	AccountBean accountBean =null;
	

	  @BeforeEach
	    void setUp() {
	        
	        accountBean = new AccountBean();
	        accountBean.setId(12);
	        accountBean.setIdentifier("identifier");
	        accountBean.setName("accountBean");
	        accountBean.setStatus(1);
	        accountBean.setUserIdDetails("userDetail");

	        
	        // mock CategoryDetailBean List Data
	        categoryDetailList = new ArrayList<>();
	        categoryDetailList.add(CategoryDetailBean.builder().id(12).accountId(1).name("getcategoryTest").identifier("identifier").build());
	        
	    }

	    @AfterEach
	    void tearDown() {
	        accountBean = null;
	        categoryDetailList =null;
	    }

	    @Test
	    void clientValidation_InvalidToken() throws ControlCenterException {
	        String expectedMessage = "ClientException : Invalid authorization token";
	        ClientException exception = assertThrows(ClientException.class, () ->
	        getAvailabilityCategoriesBL.clientValidation(null,"","tyr8-uijjf8sooijfjfkjkjskjjkje" ));
	        assertEquals(expectedMessage, exception.getMessage());
	    }

	    @Test
	    void clientValidation() throws ControlCenterException, ClientException {
	        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        UtilityBean<Object> auth = getAvailabilityCategoriesBL.clientValidation(null,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","identifier" );
	        assertEquals(auth.getAuthToken(), userId);
	    }

	    @Test
	    void clientValidation_WhenRequestIsNull() {
	        String expectedMessage = "ClientException : Invalid account identifier";
	        ClientException requestException = assertThrows(ClientException.class, () ->
	        getAvailabilityCategoriesBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje",""));
	        assertEquals(expectedMessage,requestException.getMessage());
	    }

	    @Test
	    void serverValidation() throws ServerException, ControlCenterException {
	    	 String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
	        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
		    Integer accountId = getAvailabilityCategoriesBL.serverValidation(UtilityBean.<Object>builder().pojoObject(any()).build());
	        assert(accountId.equals(12));
	    }

	    @Test
	    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
	        String expectedMessage = "ServerException : Account identifier is invalid";
	        when(accountsDao.getAccountDetailsForIdentifier(Mockito.anyString())).thenReturn(null);
	        Exception ex = assertThrows(ServerException.class, () -> getAvailabilityCategoriesBL.serverValidation(UtilityBean.<Object>builder().accountIdentifier("gggh").build()));
	        assertEquals(expectedMessage,ex.getMessage());
	    }
	    
	    
	    @Test
	    void process() throws Exception {
	        when(categoryDao.getAvailabilityKpiCategoriesByAccountId(Mockito.anyInt())).thenReturn(categoryDetailList);
	        List<GetCategory> getcategoryList = getAvailabilityCategoriesBL.process(12);
	        assertEquals(getcategoryList.size(), 1);
	        assertEquals(getcategoryList.get(0).getId(),12);
	        assertEquals(getcategoryList.get(0).getName(),"getcategoryTest");
	    }


}

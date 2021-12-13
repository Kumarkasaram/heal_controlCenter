package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UserValidationUtil;
import com.heal.controlcenter.util.ValidationsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAuditTrailBLTest {

    @InjectMocks
    GetAuditTrailBL getAuditTrailBL;
    @Mock
    GetAuditTrailBL getAuditTrailBLMock;
    @Mock
    AccountsDao accountsDao;
    @Mock
    CommonUtils commonUtils;
    @Mock
    CategoryDao categoryDao;
    @Mock
    ValidationsUtils validationsUtils;
    @Mock
    UserValidationUtil userValidationUtil;
    @Mock
    ControllerDao controllerDao;
    @Mock
    TimeZoneDao timeZoneDao;
    @Mock
    AuditTrailDao auditTrailDao;
    @Mock
    MasterDataDao masterDataDao;
    @Mock
    UserDao userDao;
    @Mock
    DateTimeUtil dateTimeUtil;

    List<ControllerBean> accessibleServiceList = null;
    AccountBean accountBean =null;
    List<ViewApplicationServiceMappingBean> mappedToApplication =null;
    List<AuditBean> auditBeanDb =null;
    List<AuditTrailPojo> auditTrailData = null;

    @BeforeEach
    void setUp() {
        accountBean = new AccountBean();
        accountBean.setId(12);
        accountBean.setIdentifier("identifier");
        accountBean.setName("accountBean");
        accountBean.setStatus(1);
        accountBean.setUserIdDetails("userDetail");

        // setting up mock data in Controller
        ControllerBean controller = new ControllerBean();
        controller.setAccountId(1);
        controller.setAppId("2");
        controller.setName("test");
        controller.setStatus(1);
        controller.setControllerTypeId(1);
        controller.setControllerTypeId(12);
        controller.setIdentifier("7640123a-fbde-4fe5-9812-581cd1e3a9c1");
        accessibleServiceList = new ArrayList<ControllerBean>();
        accessibleServiceList.add(controller);

        //mocked ViewApplicationServiceMappingBean
        mappedToApplication = new ArrayList<>();
        ViewApplicationServiceMappingBean viewApplicationServiceMappingBean = new ViewApplicationServiceMappingBean();
        viewApplicationServiceMappingBean.setApplicationId(1);
        viewApplicationServiceMappingBean.setServiceId(1);
        viewApplicationServiceMappingBean.setApplicationName("audit_trail");
        viewApplicationServiceMappingBean.setApplicationIdentifier("7640123a-fbde-4fe5-9812-581cd1e3a9c1");
        viewApplicationServiceMappingBean.setServiceName("serviceName");
        mappedToApplication.add(viewApplicationServiceMappingBean);

        // mock  AuditBean
        auditBeanDb = new ArrayList<>();
        AuditBean auditBean = new AuditBean();
        auditBean.setAuditTime("2");
        auditBean.setAuditData("data");
        auditBean.setAppId(1);
        auditBean.setBigFeatureId(1);
        auditBean.setOperationType("op");
        auditBean.setPageActionId(1);
        auditBean.setSvcId(1);
        auditBean.setBigFeatureId(1);
        auditBean.setAppId(1);
        auditBeanDb.add(auditBean);

        // mock auditTrailPojo
        auditTrailData = new ArrayList<>();
        AuditTrailPojo auditTrailPojo = new AuditTrailPojo();
        auditTrailPojo.setAuditTime(1L);
        auditTrailPojo.setApplicationName("audit_trail");
        auditTrailPojo.setOperationType("Ap");
        auditTrailPojo.setActivityType("activityType");
        auditTrailPojo.setSubActivityType("subActivityType");
        auditTrailPojo.setValue(new HashMap<>());
        auditTrailData.add(auditTrailPojo);


    }

    @AfterEach
    void tearDown() {
        accountBean = null;
        accessibleServiceList =null;
        mappedToApplication =null;
    }

    @Test
    void clientValidation_InvalidToken() throws ControlCenterException {
        String expectedMessage = "ClientException : Invalid authorization token";
        ClientException exception = assertThrows(ClientException.class, () ->
                getAuditTrailBL.clientValidation(null,"","tyr8-uijjf8sooijfjfkjkjskjjkje" ));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void clientValidation() throws ControlCenterException, ClientException {
        Map<String,String[]> requestParam = new HashMap();
        requestParam.put("toTime", new String[]{"2", "3"});
        requestParam.put("fromTime",new String[]{"2", "3"});
        requestParam.put("serviceId",new String[]{"2", "3"});
        requestParam.put("applicationId",new String[]{"2", "3"});
        requestParam.put("activityTypeId",new String[]{"2", "3"});
        requestParam.put("userId",new String[]{"2", "3"});
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        UtilityBean<AuditTrailBean> auth = getAuditTrailBL.clientValidation(requestParam,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","identifier" );
        assertEquals(auth.getAuthToken(), userId);
    }

    @Test
    void clientValidation_WhenRequestIsNull() {
        String expectedMessage = "ClientException : Invalid account identifier";
        ClientException requestException = assertThrows(ClientException.class, () ->
                getAuditTrailBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje",""));
        assertEquals(expectedMessage,requestException.getMessage());
    }

    @Test
    void serverValidation() throws ControlCenterException, ServerException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        when(commonUtils.getUserId(Mockito.anyString())).thenReturn(userId);
        when(validationsUtils.validAndGetAccount(any())).thenReturn(accountBean);
        when(userValidationUtil.getAccessibleApplicationsForUser(any(),any())).thenReturn(accessibleServiceList);
        when(controllerDao.getServicesMappedToApplication(any(),any())).thenReturn(mappedToApplication);
        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
        AuditTrailBean auditTrailBean = getAuditTrailBL.serverValidation(UtilityBean.<AuditTrailBean>builder().pojoObject(any()).authToken(any()).accountIdentifier(any()).build());
        assertEquals(1,auditTrailBean.getAccountId());
    }

    @Test
    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
        String expectedMessage = "ServerException : Account identifier is invalid";
        when(accountsDao.getAccountDetailsForIdentifier(Mockito.anyString())).thenReturn(null);
        Exception ex = assertThrows(ServerException.class, () -> getAuditTrailBL.serverValidation(UtilityBean.<AuditTrailBean>builder().accountIdentifier("gggh").build()));
        assertEquals(expectedMessage,ex.getMessage());
    }


    @Test
    void process() throws Exception {
        when(auditTrailDao.getAuditTrail(Mockito.any(),any())).thenReturn(auditBeanDb);
        when(getAuditTrailBLMock.getAuditDataList(any(),any())).thenReturn(auditTrailData);
        List<AuditTrailPojo> auditTrailPojoList = getAuditTrailBL.process(any());
        assertEquals(auditTrailPojoList.get(0).getApplicationName(),"audit_trail");
    }

}

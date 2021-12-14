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

import java.util.*;

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
    MasterTimezoneBean masterTimezoneBean =null;
    AuditTrailBean auditTrailBean =null;
    List<MasterBigFeatureBean> activityTypeList =null;
    List<MasterPageActionBean> subActivityTypeList =null;

    @BeforeEach
    void setUp() {
        accountBean = new AccountBean();
        accountBean.setId(12);
        accountBean.setIdentifier("identifier");
        accountBean.setName("accountBean");
        accountBean.setStatus(1);
        accountBean.setUserIdDetails("userDetail");

    // mock ControllerBean
        accessibleServiceList = new ArrayList<ControllerBean>();
        accessibleServiceList.add(new ControllerBean(1,"1","test1",1,1));
        accessibleServiceList.add(new ControllerBean(2,"2","test2",1,1));
        accessibleServiceList.add(new ControllerBean(3,"3","test3",1,1));


        //mocked ViewApplicationServiceMappingBean
        mappedToApplication = new ArrayList<>();
        mappedToApplication.add(new ViewApplicationServiceMappingBean(1,"appName1","appIdentifier",1,"serviceName1","",1));
        mappedToApplication.add(new ViewApplicationServiceMappingBean(1,"appNam2","appIdentifier",2,"","serviceName2",2));
        mappedToApplication.add(new ViewApplicationServiceMappingBean(1,"appName3","appIdentifier",3,"","serviceName3",3));


        //mock  AuditTrailBean
        auditTrailBean = new AuditTrailBean();
        auditTrailBean.setAccountId(1);
        auditTrailBean.setServiceIds(Arrays.asList(1,2,3));
        auditTrailBean.setAppIds(Arrays.asList(2,1,3));
        auditTrailBean.setBigFeatureIds(Arrays.asList(1,2,3));
        auditTrailBean.setFromTime(1l);
        auditTrailBean.setToTime(2l);
        auditTrailBean.setUserId("1");

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

        masterTimezoneBean = new MasterTimezoneBean();
        masterTimezoneBean.setAccountId(1);
        masterTimezoneBean.setId(1);
        masterTimezoneBean.setStatus(1);
        masterTimezoneBean.setOffsetName("offset");
        masterTimezoneBean.setTimeZoneId("1");
        masterTimezoneBean.setTimeOffset(1);

        activityTypeList = new ArrayList<>();
        activityTypeList.add(new MasterBigFeatureBean(1,"feature1","i1","des1",1,1,"dash","","",""));
        activityTypeList.add(new MasterBigFeatureBean(2,"feature1","i1","des1",1,1,"dash","","",""));
        activityTypeList.add(new MasterBigFeatureBean(3,"feature1","i1","des1",1,1,"dash","","",""));

        // mock MasterPageActionBean
        subActivityTypeList = new ArrayList<>();
        subActivityTypeList.add(new MasterPageActionBean(1,"pageAction1",1,"","",""));
        subActivityTypeList.add(new MasterPageActionBean(2,"pageAction2",1,"","",""));
        subActivityTypeList.add(new MasterPageActionBean(3,"pageAction3",1,"","",""));

    }

    @AfterEach
    void tearDown() {
        accountBean = null;
        accessibleServiceList =null;
        //mappedToApplication =null;
        masterTimezoneBean =null;
        activityTypeList =null;
        subActivityTypeList =null;
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
        when(controllerDao.getServicesMappedToApplicationByApplicationId(Mockito.anyInt(),Mockito.anyList())).thenReturn(mappedToApplication);
       // when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
        when(masterDataDao.getBigFeaturesMasterData()).thenReturn(activityTypeList);
        when(timeZoneDao.getTimezoneByAccountId(Mockito.anyInt())).thenReturn(masterTimezoneBean);
        AuditTrailBean auditTrailtrail = getAuditTrailBL.serverValidation(UtilityBean.<AuditTrailBean>builder().pojoObject(auditTrailBean).authToken("auth").accountIdentifier("identifier").build());
        assertEquals(12,auditTrailtrail.getAccountId());
    }

    @Test
    void serverValidationNoAccountFoundFound() throws ControlCenterException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        String expectedMessage = "ServerException : Invalid Account Identifier.";
        when(commonUtils.getUserId(Mockito.anyString())).thenReturn(userId);
        Exception ex = assertThrows(ServerException.class, () -> getAuditTrailBL.serverValidation(UtilityBean.<AuditTrailBean>builder().accountIdentifier(null).authToken("auth").build()));
        assertEquals(expectedMessage,ex.getMessage());
    }

    @Test
    void process() throws Exception {
        when(auditTrailDao.getAuditTrail(Mockito.any(),any())).thenReturn(auditBeanDb);
        when(masterDataDao.getBigFeaturesMasterData()).thenReturn(activityTypeList);
        when(masterDataDao.getPageActionsMasterData()).thenReturn(subActivityTypeList);
        //when(getAuditTrailBLMock.getWhereClause(Mockito.any())).thenReturn("test");
       // when(getAuditTrailBLMock.getAuditDataList(Mockito.any(),Mockito.anyList())).thenReturn(auditTrailData);
        List<AuditTrailPojo> auditTrailPojoList = getAuditTrailBL.process(auditTrailBean);
        assertEquals("feature1",auditTrailPojoList.get(0).getActivityType());
    }

}

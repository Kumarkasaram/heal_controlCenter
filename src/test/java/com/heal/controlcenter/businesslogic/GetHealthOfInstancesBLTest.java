package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.cassandra.ConfigureKpiCassandraDao;
import com.heal.controlcenter.dao.mysql.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.TagMappingDetails;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.UserValidationUtil;
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
public class GetHealthOfInstancesBLTest {
    @InjectMocks
    GetHealthOfInstancesBL getHealthOfInstancesBL;
    @Mock
    AccountsDao accountsDao;
    @Mock
    MasterDataDao masterDataDao;
    @Mock
    ComponentDao componentDao;
    @Mock
    UserValidationUtil userValidationUtil;
    @Mock
    ControllerDao controllerDao;
    @Mock
    TagsDao tagsDao;
    @Mock
    ConfigureKpiCassandraDao configureKpiCassandraDao;
    @Mock
    CommonUtils commonUtils;

    AccountBean accountBean =null;
    List<CompInstClusterDetails>  CompInstClusterDetailsList = null;
    List<CompInstClusterMappingBean> instClusterMappingList = null;
    UserAccessDetails userAccessDetails =null;
    List<ControllerBean> accessibleServiceList = null;
    UserAccessBean userAccessBean =null;
    ViewTypesBean viewTypesBean =null;
    TagDetailsBean controllerTag =null;
    List<TagMappingDetails> tagDetailsList =null;
    List<InstanceHealthDetails> instanceHealthDetails=null;


    @BeforeEach
    void setUp() {

        accountBean = new AccountBean();
        accountBean.setId(12);
        accountBean.setIdentifier("identifier");
        accountBean.setName("accountBean");
        accountBean.setStatus(1);
        accountBean.setUserIdDetails("userDetail");
        CompInstClusterDetailsList = new ArrayList<>();
        CompInstClusterDetails compInstClusterDetails =  new CompInstClusterDetails();
        compInstClusterDetails.setCompId(1);
        compInstClusterDetails.setIsCluster(0);
        compInstClusterDetails.setInstanceName("InstanceName");
        compInstClusterDetails.setComponentName("culusterComponentname_test");
        compInstClusterDetails.setStatus(1);
        compInstClusterDetails.setIdentifier("identifier");
        CompInstClusterDetailsList.add(compInstClusterDetails);

        instClusterMappingList = new ArrayList<>();
        CompInstClusterMappingBean compInstClusterMappingBean = new CompInstClusterMappingBean();
        compInstClusterMappingBean.setClusterId(1);
        compInstClusterMappingBean.setCompInstanceId(1);
        compInstClusterMappingBean.setId(12);
        compInstClusterMappingBean.setAccountId(13);
        compInstClusterMappingBean.setUserDetailsId("userDetailId");
        instClusterMappingList.add(compInstClusterMappingBean);

        //setting up mock data in  userAccessDetails
        userAccessDetails = new UserAccessDetails();
        String [] identifier  = {"7640123a-fbde-4fe5-9812-581cd1e3a9c1","a-d681ef13-d690-4917-jkhg-6c79b-1"};
        List<String> serviceIdentifiers = new ArrayList<>();
        serviceIdentifiers.add("7640123a-fbde-4fe5-9812-581cd1e3a9c1");
        serviceIdentifiers.add("a-d681ef13-d690-4917-jkhg-6c79b-2");
        userAccessDetails.setServiceIdentifiers(serviceIdentifiers);

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

        //setting up mock data in  userAccessDetails
        userAccessBean = new UserAccessBean();
        userAccessBean.setAccessDetails("{\"accounts\": [\"*\"]}");
        userAccessBean.setUserIdentifier("7640123a-fbde-4fe5-9812-581cd1e3a9c1");

        // setting up  ViewTypesBean  mock data
        viewTypesBean = new ViewTypesBean();
        viewTypesBean.setTypeId(12);
        viewTypesBean.setSubTypeId(12);
        viewTypesBean.setSubTypeName("subTypeName");
        viewTypesBean.setTypeName("typeName");

        // setting up  TagDetailsBean  mock data
        controllerTag = new TagDetailsBean();
        controllerTag.setAccountId(1);
        controllerTag.setId(1);
        controllerTag.setName("test");
        controllerTag.setTagTypeId(2);
        controllerTag.setUserDetailsId("7640123a-fbde-4fe5-9812-581cd1e3a9c1");

        // setting up  TagMappingDetails  mock data
        tagDetailsList = new ArrayList<>();
        TagMappingDetails tagMapping = new TagMappingDetails();
        tagMapping.setAccountId(2);
        tagMapping.setId(1);
        tagMapping.setTagId(0);
        tagMapping.setTagKey("test");
        tagMapping.setTagValue("value");
        tagMapping.setObjectRefTable("comp_instance");
        tagDetailsList.add(tagMapping);

        // setting up  InstanceHealthDetails mock data
        instanceHealthDetails = new ArrayList<>();
        InstanceHealthDetails instanceHealthDetails = new InstanceHealthDetails();
        instanceHealthDetails.setInstanceName("instanceName");
        instanceHealthDetails.setId(12);
        instanceHealthDetails.setDataPostStatus(1);
        instanceHealthDetails.setLastPostedTime(1L);
        instanceHealthDetails.setType("type");

    }

    @AfterEach
    void tearDown() {
        accountBean = null;
        CompInstClusterDetailsList =null;
        instClusterMappingList = null;
        userAccessDetails = null;
        tagDetailsList =null;
        accessibleServiceList =null;
        userAccessBean =null;
        viewTypesBean =null;
        instanceHealthDetails= null;

    }

    @Test
    void clientValidation_InvalidToken() throws ControlCenterException {
        String expectedMessage = "ClientException : Invalid authorization token";
        ClientException exception = assertThrows(ClientException.class, () ->
                getHealthOfInstancesBL.clientValidation(null,"","tyr8-uijjf8sooijfjfkjkjskjjkje" ));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void clientValidation() throws ControlCenterException, ClientException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        UtilityBean<Object> auth = getHealthOfInstancesBL.clientValidation(null,"7640123a-fbde-4fe5-9812-581cd1e3a9c1","identifier" );
        assertEquals(auth.getAuthToken(), userId);
    }

    @Test
    void clientValidation_WhenRequestIsNull() {
        String expectedMessage = "ClientException : Invalid account identifier";
        ClientException requestException = assertThrows(ClientException.class, () ->
                getHealthOfInstancesBL.clientValidation(null,"tyr8-uijjf8sooijfjfkjkjskjjkje",""));
        assertEquals(expectedMessage,requestException.getMessage());
    }

    @Test
    void serverValidation() throws ServerException, ControlCenterException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        when(accountsDao.getAccountDetailsForIdentifier(any())).thenReturn(accountBean);
        when(commonUtils.getUserId(any())).thenReturn(userId);
        UtilityBean<Object> utility = getHealthOfInstancesBL.serverValidation(UtilityBean.<Object>builder().account(accountBean).authToken(userId).build());
        assert(utility.getUserId().equals("7640123a-fbde-4fe5-9812-581cd1e3a9c1"));
    }

    @Test
    void serverValidationNoAccountFFoundFound() throws ControlCenterException {
        String expectedMessage = "ServerException : Account identifier is invalid";
        when(accountsDao.getAccountDetailsForIdentifier(Mockito.anyString())).thenReturn(null);
        Exception ex = assertThrows(ServerException.class, () -> getHealthOfInstancesBL.serverValidation(UtilityBean.<Object>builder().accountIdentifier("gggh").build()));
        assertEquals(expectedMessage,ex.getMessage());
    }

    @Test
    void process() throws Exception {
        when(masterDataDao.getCompInstanceDetails(Mockito.anyInt())).thenReturn(CompInstClusterDetailsList);
        when(componentDao.getInstanceClusterMapping(Mockito.anyInt())).thenReturn(instClusterMappingList);
        when(userValidationUtil.getUserAccessDetails(Mockito.anyString(),Mockito.anyString())).thenReturn(userAccessDetails);
        when(masterDataDao.getViewTypesFromMstTypeAndSubTypeName(Mockito.anyString(),Mockito.anyString())).thenReturn(viewTypesBean);
        when(tagsDao.getTagDetails(Mockito.anyString())).thenReturn(controllerTag);
        when(tagsDao.getTagMappingDetails(Mockito.anyInt())).thenReturn(tagDetailsList);
        when(configureKpiCassandraDao.getInstanceHealthMapForAccount(Mockito.anyString(),Mockito.anyString())).thenReturn(1L);
        when(controllerDao.getControllerDetailsWithIdentifier(Mockito.anyInt(),Mockito.anyList())).thenReturn(accessibleServiceList);
       assertEquals("InstanceName",getHealthOfInstancesBL.process(UtilityBean.builder().account(accountBean).authToken("accountIdentifier").userId("userID").build()).get(0).getInstanceName());

    }

}

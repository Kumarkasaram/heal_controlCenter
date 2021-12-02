package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UserAccessibleActions;
import com.heal.controlcenter.beans.UserAttributesBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.*;
import com.heal.controlcenter.util.CommonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccessibleActionBLTest {

    @Autowired
    @InjectMocks
    UserAccessibleActionBL userAccessibleActionBL;
    @Mock
    CommonUtils commonUtils;
    @Mock
    UserDao userAccessDataDao;
    @Mock
    UserAttributesBean mockUserAttributesBean;


    @BeforeEach
    void setUp() {
        mockUserAttributesBean.setAccessProfileId(1);
        mockUserAttributesBean.setRoleName("Super Admin");
        mockUserAttributesBean.setAccessProfileName("Super Admin");
        mockUserAttributesBean.setAccessProfileId(1);
    }

    @AfterEach
    void tearDown() {
        mockUserAttributesBean = null;
    }

    @Test
    void clientValidation_InvalidToken() throws ControlCenterException {
        String expectedMessage = "ClientException : Invalid Authorization token";
        when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(any());
        ClientException exception = assertThrows(ClientException.class, () ->
                        userAccessibleActionBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje" ));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void clientValidation() throws ControlCenterException, ClientException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(userId);
        UtilityBean<String> auth = userAccessibleActionBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje" );
        assertEquals(auth.getPojoObject(), userId);
    }

    @Test
    void clientValidation_WhenRequestIsNull() {
        String expectedMessage = "ClientException : Invalid Authorization token";
        ClientException requestException = assertThrows(ClientException.class, () ->
            userAccessibleActionBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje"));
        assertTrue(expectedMessage.contains(requestException.getMessage()));
    }

    @Test
    void serverValidation() throws ServerException, ControlCenterException {
        when(userAccessDataDao.getRoleProfileInfoForUserId(any())).thenReturn(mockUserAttributesBean);
        UserAttributesBean userAttributesBean = userAccessibleActionBL.serverValidation(UtilityBean.<String>builder().pojoObject(any()).build());
        assert(userAttributesBean).equals(mockUserAttributesBean);
    }

    @Test
    void serverValidationNoRoleProfileFound() throws ControlCenterException {
        String expectedMessage = "ServerException : User details unavailable";
        when(userAccessDataDao.getRoleProfileInfoForUserId(any())).thenReturn(null);
        Exception ex = assertThrows(ServerException.class, () -> userAccessibleActionBL.serverValidation(UtilityBean.<String>builder().pojoObject(any()).build()));
        assertTrue(expectedMessage.contains(ex.getMessage()));
    }

    @Test
    void getUserAccessibleActions() throws ControlCenterException, DataProcessingException {
        List<String> mockAllowedActions = new ArrayList<String>() {{ add("action_1"); add("action_2"); }};
        when(userAccessDataDao.getUserAccessibleActions(mockUserAttributesBean.getAccessProfileId())).thenReturn(mockAllowedActions);
        UserAccessibleActions userAccessibleActions = userAccessibleActionBL.process(mockUserAttributesBean);
        assertEquals(userAccessibleActions.getAllowedActions().size(), mockAllowedActions.size());
        assertEquals(userAccessibleActions.getProfile(), mockUserAttributesBean.getAccessProfileName());
    }
}
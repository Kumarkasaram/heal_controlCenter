package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UserDetailsBean;
import com.heal.controlcenter.beans.UserNotificationDetailsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.NotificationPreferencesDataDao;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.util.CommonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * @author Sourav Suman - 15-11-2021
 */

@ExtendWith(MockitoExtension.class)
@PrepareForTest({UserDetailsBL.class})
class UserDetailsBLTest {

    @Autowired
    @InjectMocks
    UserDetailsBL userDetailsBL;
    @Mock
    CommonUtils commonUtils;
    @Mock
    UserDao userDao;
    @Mock
    NotificationPreferencesDataDao notificationPreferencesDataDao;
    UserDetailsBean mockBean;
    List<UserDetailsBean> mockListBean;
    UserNotificationDetailsBean mockNotificationBean;
    Map<String, UserNotificationDetailsBean> mockuserNotificationDetailsBeanMap;

    @BeforeEach
    void setUp() {
        mockBean = new UserDetailsBean();
        mockListBean = new ArrayList<>();
        mockNotificationBean = new UserNotificationDetailsBean();
        mockuserNotificationDetailsBeanMap = new HashMap<>();
        mockBean.setSmsNotification(1);
        mockBean.setEmailNotification(1);
        mockBean.setForensicNotification(0);
        mockBean.setRole("mock role");
        mockBean.setUpdatedBy("appsoneadmin");
        mockBean.setUserId("user id mock");
        mockBean.setUserName("username1");
        mockListBean.add(mockBean);
        mockNotificationBean.setForensicEnabled(0);
        mockNotificationBean.setEmailEnabled(1);
        mockNotificationBean.setSmsEnabled(1);
        mockNotificationBean.setAccountId(1);
        mockNotificationBean.setApplicableUserId("user id mock");
        mockuserNotificationDetailsBeanMap.put("user id mock", mockNotificationBean);
    }

    @AfterEach
    void tearDown() {
        mockListBean = null;
        mockBean = null;
        mockuserNotificationDetailsBeanMap = null;
        mockNotificationBean = null;
    }

    @Test
    void clientValidation_InvalidToken() throws ControlCenterException {
        String expectedMessage = "ClientException : Invalid Authorization token";
        when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(any());
        ClientException exception = assertThrows(ClientException.class, () ->
                userDetailsBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje" ));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void clientValidation() throws ControlCenterException, ClientException {
        String userId = "7640123a-fbde-4fe5-9812-581cd1e3a9c1";
        when(commonUtils.getUserId("tyr8-uijjf8sooijfjfkjkjskjjkje")).thenReturn(userId);
        UtilityBean<String> auth = userDetailsBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje" );
        assertEquals(auth.getPojoObject(), userId);
    }

    @Test
    void clientValidation_WhenRequestIsNull() {
        String expectedMessage = "ClientException : Invalid Authorization token";
        ClientException requestException = assertThrows(ClientException.class, () ->
                userDetailsBL.clientValidation(null, "tyr8-uijjf8sooijfjfkjkjskjjkje"));
        assertTrue(expectedMessage.contains(requestException.getMessage()));
    }

    @Test
    void process_when_user_list_is_non_empty() throws Exception {
        when(userDao.getNonSuperUsers()).thenReturn(mockListBean);
        when(notificationPreferencesDataDao.getEmailSmsForensicNotificationStatusForUsers()).thenReturn(mockuserNotificationDetailsBeanMap);
        PowerMockito.when(userDetailsBL, "getUserName", "appsoneadmin").thenReturn("admin");
        List<UserDetailsBean> userBeanList = userDetailsBL.process("users");
        assertEquals(userBeanList.size(), 1);
        assertEquals(userBeanList.get(0).getUserId(),"user id mock");
        assertEquals(userBeanList.get(0).getUpdatedBy(),"admin");
        assertEquals(userBeanList.get(0).getUserName(),"username1");
        assertEquals(userBeanList.get(0).getEmailNotification(),1);
    }

    @Test
    void process_when_user_list_is_empty() throws DataProcessingException, ControlCenterException {
        when(userDao.getNonSuperUsers()).thenReturn(new ArrayList<UserDetailsBean>());
        List<UserDetailsBean> userBeanList = userDetailsBL.process("users");
        assertEquals(userBeanList.size(), 0);
    }

    @Test
    void process_when_error_happen() throws ControlCenterException {
        String errorMessage = "Exception encountered while fetching users";
        given(userDao.getNonSuperUsers()).willAnswer(exc -> {
            throw new ControlCenterException("Exception encountered while fetching users");
        });
        Throwable processing = assertThrows(DataProcessingException.class, () -> {
            userDetailsBL.process("users");
        });
        assertTrue(processing.getMessage().contains(errorMessage));
    }

    @Test
    void getUserName_test() throws NoSuchMethodException, ControlCenterException, InvocationTargetException, IllegalAccessException {
        Method methodDetails = UserDetailsBL.class.getDeclaredMethod("getUserName", String.class);
        methodDetails.setAccessible(true);
        when(userDao.getUsernameFromIdentifier(anyString())).thenReturn("appsoneadmin");
        String actual = (String) methodDetails.invoke(userDetailsBL, "admin");
        assertEquals("appsoneadmin",actual);
    }
}
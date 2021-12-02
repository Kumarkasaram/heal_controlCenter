package com.heal.controlcenter.scheduler;

import com.appnomic.appsone.keycloak.KeycloakConnectionManager;
import com.heal.controlcenter.beans.UserInfoBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.util.DateTimeUtil;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration
public class DormantServiceTest {

    @InjectMocks
    private UserDormantServiceScheduler userDormantServiceScheduler;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    List<UserInfoBean> userBeanList;

    @Mock
    DateTimeUtil dateTimeUtil;

    @Mock
    UserDao userDataDao;

    @Mock
    UserInfoBean userInfoBean;

    List<UserInfoBean> mockUserInfoBean = new ArrayList<>();

    UserInfoBean userInfo = new UserInfoBean();

    @BeforeEach
    void setUp(){
        userInfo.setEmailId("test@appnomic.com");
        userInfo.setUserName("appsoneadmin");
        userInfo.setFirstName("test");
        userInfo.setLastLoginTime(new Date().toString());
        mockUserInfoBean.add(userInfo);
    }

    /*Test cases steps that can be covered:
    * 1. Mock the output of userDetailsDao.getActiveUsers()
    *   - Mock it to return a valid user who should not be disabled
    *   - Mock it to return a valid user who should be disabled. There are 2 use cases:
    *       - Who has not logged into the system at all in 30 days
    *       - Who has logged into but not accessed the system in 90 days
    **/

    @Test
    void validUsers() throws ControlCenterException, ParseException {
        userInfo.setEmailId("test@appnomic.com");
        userInfo.setUserName("appsoneadmin");
        userInfo.setFirstName("test");
        userInfo.setId("userId");
        userInfo.setStatus(1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        userInfo.setLastLoginTime(simpleDateFormat.format(System.currentTimeMillis()));

        ArrayList<UserInfoBean> list = new ArrayList<>();
        list.add(userInfo);

        when(userDataDao.getActiveUsers()).thenReturn(list);
        when(userDataDao.getSuperAdmin()).thenReturn(userInfo);

        long time = new DateTime().withTimeAtStartOfDay().getMillis();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTime = simpleDateFormat.format(time);
        when(dateTimeUtil.getDateInGMT(time)).thenReturn(simpleDateFormat.parse(dateTime));

        userDormantServiceScheduler.findDormantUser();

        assertEquals(userInfo.getStatus(), 1);
    }

    @Test
    void inactiveUser_withLogin() throws ControlCenterException, ParseException {
        userInfo.setEmailId("test@appnomic.com");
        userInfo.setUserName("appsoneadmin");
        userInfo.setFirstName("test");
        userInfo.setId("userId");
        userInfo.setStatus(1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        userInfo.setLastLoginTime(simpleDateFormat.format(System.currentTimeMillis()-(86400000*90L)));

        ArrayList<UserInfoBean> list = new ArrayList<>();
        list.add(userInfo);

        when(userDataDao.getActiveUsers()).thenReturn(list);
        when(userDataDao.getSuperAdmin()).thenReturn(userInfo);

        long time = new DateTime().withTimeAtStartOfDay().getMillis();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTime = simpleDateFormat.format(time);
        when(dateTimeUtil.getDateInGMT(time)).thenReturn(simpleDateFormat.parse(dateTime));

        try(MockedStatic<KeycloakConnectionManager> manager = Mockito.mockStatic(KeycloakConnectionManager.class)) {
            manager.when(() -> KeycloakConnectionManager.editKeycloakUser(anyString(), anyString())).thenReturn("Success");
        }

        userDormantServiceScheduler.findDormantUser();

        assertEquals(userInfo.getStatus(), 0);
    }

    @Test
    void inactiveUser_withoutLogin() throws ControlCenterException, ParseException {
        userInfo.setEmailId("test@appnomic.com");
        userInfo.setUserName("appsoneadmin");
        userInfo.setFirstName("test");
        userInfo.setId("userId");
        userInfo.setStatus(1);
        userInfo.setLastLoginTime(null);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        userInfo.setCreatedTime(simpleDateFormat.format(System.currentTimeMillis()-(86400000*31L)));

        ArrayList<UserInfoBean> list = new ArrayList<>();
        list.add(userInfo);

        when(userDataDao.getActiveUsers()).thenReturn(list);
        when(userDataDao.getSuperAdmin()).thenReturn(userInfo);

        long time = new DateTime().withTimeAtStartOfDay().getMillis();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTime = simpleDateFormat.format(time);
        when(dateTimeUtil.getDateInGMT(time)).thenReturn(simpleDateFormat.parse(dateTime));

        try(MockedStatic<KeycloakConnectionManager> manager = Mockito.mockStatic(KeycloakConnectionManager.class)) {
            manager.when(() -> KeycloakConnectionManager.editKeycloakUser(anyString(), anyString())).thenReturn("Success");
        }

        userDormantServiceScheduler.findDormantUser();

        assertEquals(userInfo.getStatus(), 0);
    }

    @Test
    void inactiveUser_invalidLastLogin() throws ControlCenterException, ParseException {
        userInfo.setEmailId("test@appnomic.com");
        userInfo.setUserName("appsoneadmin");
        userInfo.setFirstName("test");
        userInfo.setId("userId");
        userInfo.setStatus(1);
        userInfo.setLastLoginTime(" ");

        userInfo.setCreatedTime(null);

        ArrayList<UserInfoBean> list = new ArrayList<>();
        list.add(userInfo);

        when(userDataDao.getActiveUsers()).thenReturn(list);
        when(userDataDao.getSuperAdmin()).thenReturn(userInfo);

        long time = new DateTime().withTimeAtStartOfDay().getMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateTime = simpleDateFormat.format(time);
        when(dateTimeUtil.getDateInGMT(time)).thenReturn(simpleDateFormat.parse(dateTime));

        try(MockedStatic<KeycloakConnectionManager> manager = Mockito.mockStatic(KeycloakConnectionManager.class)) {
            manager.when(() -> KeycloakConnectionManager.editKeycloakUser(anyString(), anyString())).thenReturn("Success");
        }

        userDormantServiceScheduler.findDormantUser();

        assertEquals(userInfo.getStatus(), 1);
    }

   /*@Test
    void getActiveUser()  {
        when(jdbcTemplate.query(ArgumentMatchers.anyString(), ArgumentMatchers.any(BeanPropertyRowMapper.class))).thenReturn(userBeanList);
        List<UserInfoBean> mockUserInfoBean = userDataDao.getActiveUsers();
        assertEquals(mockUserInfoBean.size(), userBeanList.size());
    }

    /*@Test
    void getSuperAdmin() {
        String GET_SUPER_ADMIN_QUERY = "SELECT  u.user_identifier id, u.created_time createdTime, u.last_login_time lastLoginTime, " +
                "u.is_timezone_mychoice isTimezoneMychoice, u.is_notifications_timezone_mychoice isNotificationsTimezoneMychoice, " +
                "un.FIRST_NAME firstName, un.LAST_NAME lastName, u.username userName, u.email_address emailId " +
                "FROM user_attributes u, appsonekeycloak.USER_ENTITY un " +
                "where u.status = 1 and un.id= u.user_identifier and u.mst_access_profile_id = 1";

        when(jdbcTemplate.queryForObject(GET_SUPER_ADMIN_QUERY, Object.class, new Object[]{})).thenReturn(userInfo);
        UserInfoBean mockUserInfoData = userDataDao.getSuperAdmin();
        assertEquals(mockUserInfoData.getUserName(), "appsoneadmin");
        assertEquals(mockUserInfoData.getFirstName(), "Test");
    }*/

}

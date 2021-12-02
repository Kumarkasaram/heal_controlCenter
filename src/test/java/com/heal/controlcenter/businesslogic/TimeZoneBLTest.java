package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.TimezoneBean;
import com.heal.controlcenter.dao.mysql.TimeZoneDao;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeZoneBLTest {

    @Mock
    TimeZoneDao timeZoneDao;

    @InjectMocks
    TimeZoneBL timeZoneBL;

    List<TimezoneBean> listOfTimeZones = new ArrayList<>();

    @BeforeEach
    void setUp() {
        TimezoneBean timezoneBean = new TimezoneBean();
        timezoneBean.setAccountId(124667);
        timezoneBean.setOffset(788999772);
        timezoneBean.setUserDetailsId("UserId 1");
        timezoneBean.setStatus(0);
        listOfTimeZones.add(timezoneBean);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testServerValidation() throws DataProcessingException, ControlCenterException {
        when(timeZoneDao.getTimeZones()).thenReturn(listOfTimeZones);
        List<TimezoneBean> mockList = timeZoneBL.process("time zone");
        assertEquals(mockList.size(), listOfTimeZones.size());
    }

    @Test
    void testServerValidation_WhenBadRequest() throws ControlCenterException {
        given(timeZoneDao.getTimeZones()).willAnswer( exc -> { throw new ControlCenterException("Test exception"); });
        Throwable exception = assertThrows(DataProcessingException.class, () -> {
            timeZoneBL.process("timezone");
        });
        assertEquals("DataProcessingException : Test exception", exception.getMessage());
    }
}
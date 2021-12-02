package com.heal.controlcenter.businesslogic;

import com.appnomic.appsone.util.ConfProperties;
import com.heal.controlcenter.dao.mysql.ServiceConfigurationDao;
import com.heal.controlcenter.beans.ServiceConfigurationBean;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ServiceConfigurationBL {

    @Autowired
    ServiceConfigurationDao serviceConfigurationDao;
    @Autowired
    DateTimeUtil dateTimeUtil;

    private static final int SERVICE_START_HOUR = ConfProperties.getInt(Constants.SERVICE_START_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_START_TIME_WITHIN_AN_HOUR);
    private static final int SERVICE_END_HOUR = ConfProperties.getInt(Constants.SERVICE_END_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_END_TIME_WITHIN_AN_HOUR);
    private static final int SERVICE_START = ConfProperties.getInt(Constants.SERVICE_START_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_START_TIME_MORE_THAN_AN_HOUR);
    private static final int SERVICE_END = ConfProperties.getInt(Constants.SERVICE_END_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_END_TIME_MORE_THAN_AN_HOUR);


    private static final int SOR_PERSISTENCE_HOUR = ConfProperties.getInt(Constants.SERVICE_SOR_PERSISTENCE_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_SOR_PERSISTENCE_WITHIN_AN_HOUR);
    private static final int SOR_SUPPRESSION_HOUR = ConfProperties.getInt(Constants.SERVICE_SOR_SUPPRESSION_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_SOR_SUPPRESSION_WITHIN_AN_HOUR);
    private static final int SOR_PERSISTENCE = ConfProperties.getInt(Constants.SERVICE_SOR_PERSISTENCE_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_SOR_PERSISTENCE_MORE_THAN_AN_HOUR);
    private static final int SOR_SUPPRESSION = ConfProperties.getInt(Constants.SERVICE_SOR_SUPPRESSION_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_SOR_SUPPRESSION_MORE_THAN_AN_HOUR);

    private static final int NOR_PERSISTENCE_HOUR = ConfProperties.getInt(Constants.SERVICE_NOR_PERSISTENCE_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_NOR_PERSISTENCE_WITHIN_AN_HOUR);
    private static final int NOR_SUPPRESSION_HOUR = ConfProperties.getInt(Constants.SERVICE_NOR_SUPPRESSION_WITHIN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_NOR_SUPPRESSION_WITHIN_AN_HOUR);
    private static final int NOR_PERSISTENCE = ConfProperties.getInt(Constants.SERVICE_NOR_PERSISTENCE_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_NOR_PERSISTENCE_MORE_THAN_AN_HOUR);
    private static final int NOR_SUPPRESSION = ConfProperties.getInt(Constants.SERVICE_NOR_SUPPRESSION_MORE_THAN_AN_HOUR_PROPERTY_NAME,
            Constants.SERVICE_NOR_SUPPRESSION_MORE_THAN_AN_HOUR);

    public void addServiceConfiguration(int serviceId, int accountId, String userId) throws ControlCenterException {
        List<ServiceConfigurationBean> list = new ArrayList<>();

        list.add(ServiceConfigurationBean.builder()
                .serviceId(serviceId)
                .accountId(accountId)
                .userDetailsId(userId)
                .createdTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                .updatedTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                .startCollectionInterval(SERVICE_START_HOUR)
                .endCollectionInterval(SERVICE_END_HOUR)
                .sorPersistence(SOR_PERSISTENCE_HOUR)
                .sorSuppression(SOR_SUPPRESSION_HOUR)
                .norPersistence(NOR_PERSISTENCE_HOUR)
                .norSuppression(NOR_SUPPRESSION_HOUR)
                .build());

        list.add(ServiceConfigurationBean.builder()
                .serviceId(serviceId)
                .accountId(accountId)
                .userDetailsId(userId)
                .createdTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                .updatedTime(dateTimeUtil.getTimeInGMT(System.currentTimeMillis()))
                .startCollectionInterval(SERVICE_START)
                .endCollectionInterval(SERVICE_END)
                .sorPersistence(SOR_PERSISTENCE)
                .sorSuppression(SOR_SUPPRESSION)
                .norPersistence(NOR_PERSISTENCE)
                .norSuppression(NOR_SUPPRESSION)
                .build());

        serviceConfigurationDao.addServiceConfiguration(list);
    }
}

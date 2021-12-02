package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.ServiceConfigurationBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class ServiceConfigurationDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int addServiceConfiguration(List<ServiceConfigurationBean> serviceDetails) throws ControlCenterException {
        String query = "INSERT INTO service_configurations ( service_id,account_id, user_details_id, created_time, " +
                "updated_time, start_collection_interval, end_collection_interval, sor_persistence, sor_suppression, " +
                "nor_persistence, nor_suppression) " +
                "VALUES ( :serviceId,:accountId, :userDetailsId, :createdTime, :updatedTime, :startCollectionInterval, " +
                ":endCollectionInterval, :sorPersistence, :sorSuppression, :norPersistence, :norSuppression)";
        try {
            log.debug("Adding service configurations");
            return jdbcTemplate.update(query, serviceDetails);
        } catch (Exception ex) {
            throw new ControlCenterException("Error in adding service configurations");
        }
    }
}

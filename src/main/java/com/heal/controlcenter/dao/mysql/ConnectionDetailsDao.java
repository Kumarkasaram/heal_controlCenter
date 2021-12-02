package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.ConnectionDetailsBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class ConnectionDetailsDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<ConnectionDetailsBean> getConnectionsByAccountId(Integer accountId) {
        String query = "select id, source_id as sourceId, source_ref_object as sourceRefObject, destination_id as destinationId, " +
                "destination_ref_object as destinationRefObject, created_time as createdTime, updated_time as updatedTime, " +
                "account_id as accountId, user_details_id as userDetailsId, is_discovery as isDiscovery from connection_details " +
                "where account_id = " + accountId;
        try {
            log.debug("getting connections.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ConnectionDetailsBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching connections from 'connection_details' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
        }
        return Collections.emptyList();
    }
}

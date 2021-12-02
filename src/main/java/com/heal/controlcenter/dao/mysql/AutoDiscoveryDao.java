package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.AutoDiscoveryDiscoveredConnectionsBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class AutoDiscoveryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<AutoDiscoveryDiscoveredConnectionsBean> getDiscoveredConnectionsList() {
        String query = "select adc.id, adc.source_identifier sourceIdentifier, adc.destination_identifier destinationIdentifier, ah.last_discovery_run_time " +
                "lastUpdatedTime, adc.is_discovery isDiscovery, adc.discovery_status discoveryStatus from autodisco_discovered_connections adc, " +
                "autodisco_host ah where adc.host_identifier = ah.host_identifier and ah.is_ignored = 0";
        try {
            log.debug("getting discovered connections [Auto Discovery].");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(AutoDiscoveryDiscoveredConnectionsBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching connections from 'autodisco_discovered_connections' table. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
        }
        return Collections.emptyList();
    }
}

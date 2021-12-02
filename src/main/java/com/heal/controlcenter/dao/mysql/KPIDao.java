package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class KPIDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer getKpiCountForAccount(int accountId) throws ControlCenterException {
        try {
            String query = "select count(1) from mst_kpi_details where account_id in (1, " + accountId + ")";
            return jdbcTemplate.queryForObject(query, Integer.class);
        } catch (Exception e) {
            log.error("Exception while getting KPI count for accountId [{}]. Details: ", accountId, e);
            throw new ControlCenterException("Error occurred while getting KPI count.");
        }
    }
}

package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ActionScriptDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer getForensicCountForAccount(int accountId) throws ControlCenterException {
        String query = "select count(distinct Actions.id)  from actions as Actions, mst_sub_type as MasterSubType "+
                "where MasterSubType.name = 'Forensic' and Actions.account_id in (1, " + accountId + ")";
        try {
            log.debug("getting forensic count.");
            return jdbcTemplate.queryForObject(query, Integer.class);
        } catch (Exception e) {
            log.error("Exception encountered while fetching forensic count from 'actions' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error occurred while getting forensic count.");
        }
    }
}

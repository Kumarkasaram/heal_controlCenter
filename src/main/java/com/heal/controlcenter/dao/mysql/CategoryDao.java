package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CategoryDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer getCategoryCountForAccount(Integer accountId) throws ControlCenterException {
        String query = "select count(id) from mst_category_details where account_id in (1, " + accountId + ")";
        try {
            log.debug("getting category count.");
            return jdbcTemplate.queryForObject(query, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            log.info("Categories unavailable for accountId [{}]", accountId);
            return 0;
        } catch (Exception e) {
            log.error("Exception encountered while fetching category count from 'mst_category_details' table " +
                    "for accountId [{}]. Details: ", accountId, e);
            throw new ControlCenterException("Error occurred while getting category count.");
        }
    }
}

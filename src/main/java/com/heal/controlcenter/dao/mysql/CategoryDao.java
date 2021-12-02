package com.heal.controlcenter.dao.mysql;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.CategoryDetailBean;
import com.heal.controlcenter.exception.ControlCenterException;

import lombok.extern.slf4j.Slf4j;

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
    public List<CategoryDetailBean>  getAvailabilityKpiCategoriesByAccountId(Integer accountId) throws ControlCenterException {
        String query = "select distinct mcd.name, vkcd.category_id id, vkcd.is_workload isWorkLoad from view_kpi_category_details vkcd, mst_category_details mcd " +
                "where kpi_type_id=29 and mcd.id=vkcd.category_id and mcd.account_id in (1, ?)";
        try {
            log.debug("getting category count.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(CategoryDetailBean.class),accountId);
        } catch (DataAccessException e) {
            log.error("Error in CategoryDao while fetching AvailabilityKpiCategories  information for user [{}]. Details: ", accountId, e);
            throw new ControlCenterException("Error in CategoryDao  with id."+accountId);

        }
    }
    
}

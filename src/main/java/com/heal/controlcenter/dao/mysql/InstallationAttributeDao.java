package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.InstallationAttributeBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class InstallationAttributeDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<InstallationAttributeBean> getInstallationAttributes() throws ControlCenterException {
        String query = "select name, value from a1_installation_attributes";
        try {
            log.debug("getting installation details");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstallationAttributeBean.class));
        } catch (Exception ex) {
            log.error("Error in fetching installation attribute. Details: {}, Stack trace: {}", ex.getMessage(), ex.getStackTrace());
            throw new ControlCenterException("Error in fetching installation attribute details");
        }
    }
}

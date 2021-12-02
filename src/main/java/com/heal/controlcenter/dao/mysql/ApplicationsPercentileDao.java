package com.heal.controlcenter.dao.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ApplicationsPercentileDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

}

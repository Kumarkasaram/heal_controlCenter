package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.MasterTimezoneBean;
import com.heal.controlcenter.beans.TimezoneBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class TimeZoneDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TimezoneBean> getTimeZones() throws ControlCenterException {
        try {
            String ALL_TIME_ZONES_QUERY = "select id, time_zone_id timeZoneId, timeoffset offset, user_details_id userDetailsId, " +
                    "account_id accountId, status status from mst_timezone where status=1";
            return jdbcTemplate.query(ALL_TIME_ZONES_QUERY, new BeanPropertyRowMapper<>(TimezoneBean.class));
        } catch (Exception e) {
            log.error("Error while getting time zone. Details: {}, Stack trace: {}", e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error in fetching timezones");
        }
    }
    public MasterTimezoneBean getTimezoneByAccountId(int accountId) {
        try {
            String query = "select mt.id id, mt.timeoffset timeOffset, mt.time_zone_id timeZoneId, mt.account_id accountId " +
                    "from tag_mapping tm, mst_timezone mt, tag_details td " +
                    "where tm.object_id = ? and tm.object_ref_table = 'account' and tm.tag_id = td.id " +
                    "and td.name='Timezone' and mt.id = tm.tag_key";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(MasterTimezoneBean.class),accountId);
        } catch (Exception e) {
            log.error("Invalid input parameter/s provided. Details:  accountId [{}]. Details: ",  accountId, e);
            return null;
        }
    }

}

package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.AuditBean;
import com.heal.controlcenter.beans.AuditTrailBean;
import com.heal.controlcenter.exception.ControlCenterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditTrailDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<AuditBean> getAuditTrail(AuditTrailBean bean, String whereClause) throws ControlCenterException {
        String query = "select mst_big_feature_id bigFeatureId, mst_page_action_id pageActionId,application_id appId, service_id svcId, unix_timestamp(audit_time), " +
        "audit_time auditTime, audit_user updatedBy, operation operationType, audit_data auditData " +
                "from audit_data where <"+whereClause+"> " +
                "audit_time BETWEEN CONVERT_TZ(from_unixtime("+bean.getFromTime()+"),"+bean.getTimeZone()+","+bean.getDefaultTimeZone()+") " +
                "and CONVERT_TZ(from_unixtime("+bean.getToTime()+"),"+bean.getTimeZone()+", "+bean.getDefaultTimeZone()+")";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(AuditBean.class));
        } catch (Exception ex) {
            throw new ControlCenterException("Error in fetching audit trail");
        }
    }

}

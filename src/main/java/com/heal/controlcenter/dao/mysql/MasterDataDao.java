package com.heal.controlcenter.dao.mysql;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.heal.controlcenter.beans.CompInstClusterDetailsBean;
import com.heal.controlcenter.beans.MasterComponentBean;
import com.heal.controlcenter.beans.MasterSubTypeBean;
import com.heal.controlcenter.beans.TimezoneBean;
import com.heal.controlcenter.beans.ViewTypesBean;
import com.heal.controlcenter.exception.ControlCenterException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MasterDataDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public TimezoneBean getTimeZoneWithId(String timeZoneId) throws ControlCenterException {
        String query = "select id, time_zone_id timeZoneId, timeoffset offset, user_details_id userDetailsId, account_id accountId, " +
                "status status from mst_timezone where status=1 and time_zone_id=?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(TimezoneBean.class), timeZoneId);
        } catch (Exception ex) {
            log.error("Error while fetching timezone details for id [{}]. Details: ", timeZoneId, ex);
            throw new ControlCenterException("Error while fetching timezone details");
        }
    }

    public ViewTypesBean getMstSubTypeBySubTypeId(int subTypeId) {
        String query = "select type typeName, typeid typeId, name subTypeName, subtypeid subTypeId from view_types " +
                "where subtypeid = " + subTypeId;
        try {
            log.debug("getting type details.");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ViewTypesBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching master type and sub type info from 'view_types' table for subTypeId [{}]. Details: {}, Stack trace: {}", subTypeId, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public ViewTypesBean getViewTypesFromMstTypeAndSubTypeName(String typeName, String subTypeName) throws ControlCenterException {
        String query = "select type typeName, typeid typeId, name subTypeName, subtypeid subTypeId from view_types where type=? and name=?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ViewTypesBean.class), typeName, subTypeName);
        } catch (Exception e) {
            log.error("Error while fetching view type using typeName [{}] and subTypeName [{}]", typeName, subTypeName);
            throw new ControlCenterException("Error while fetching view type");
        }
    }

    public MasterSubTypeBean getMasterSubTypeDetailsForId(int subTypeId) {
        MasterSubTypeBean masterSubTypeDetailsForId = null;
        try {
            String GET_MASTER_SUBTYPE_DETAILS_FOR_ID_QUERY = "select id, name, mst_type_id mstTypeId, created_time createdTime, " +
                    "updated_time updatedTime, user_details_id userDetailsId, account_id accountId, description, is_custom isCustom, " +
                    "status from mst_sub_type where id =" + subTypeId;
            masterSubTypeDetailsForId = jdbcTemplate.queryForObject(GET_MASTER_SUBTYPE_DETAILS_FOR_ID_QUERY,
                    new BeanPropertyRowMapper<>(MasterSubTypeBean.class));
        } catch (Exception e) {
            log.error("Error while getting master subtype details for id. Reason: {}", e.getMessage(), e);
        }
        return masterSubTypeDetailsForId;
    }


    public List<ViewTypesBean> getMstTypeByTypeName(String typeName) {
        String query = "select type typeName, typeid typeId, name subTypeName, subtypeid subTypeId from view_types where type=?";
        try {
            log.debug("getting type details.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ViewTypesBean.class), typeName);
        } catch (Exception e) {
            log.error("Error occurred while fetching master type and sub type info from 'view_types' table for typeName [{}]. Details: {}, Stack trace: {}", typeName, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public List<CompInstClusterDetailsBean> getCompInstanceDetails(Integer accountId) {
        String query = "select id instanceId,common_version_id commonVersionId, common_version_name commonVersionName,mst_component_id compId,component_name componentName, " +
                "mst_component_type_id mstComponentTypeId,component_type_name componentTypeName, " +
                "mst_component_version_id compVersionId,component_version_name componentVersionName,name instanceName,host_id hostId,status, " +
                "host_name hostName,is_cluster isCluster,identifier, " +
                "host_address hostAddress, supervisor_id supervisorId from view_component_instance where account_id = " + accountId + " and status = 1";
        try {
            log.debug("getting component instance details.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(CompInstClusterDetailsBean.class));
        } catch (Exception e) {
            log.error("Error occurred while fetching component instance list from 'view_component_instance' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
        }
        return Collections.emptyList();
    }

    public List<MasterSubTypeBean> getMasterSubTypeDetailsList() {
        List<MasterSubTypeBean> masterSubTypeDetailsForId = null;
        try {
            String GET_MASTER_SUBTYPE_DETAILS_FOR_ID_QUERY = "select id, name, mst_type_id mstTypeId, created_time createdTime, " +
                    "updated_time updatedTime, user_details_id userDetailsId, account_id accountId, description, is_custom isCustom, " +
                    "status from mst_sub_type where mst_type_id=1 and status=1";
            masterSubTypeDetailsForId = jdbcTemplate.query(GET_MASTER_SUBTYPE_DETAILS_FOR_ID_QUERY,
                    new BeanPropertyRowMapper<>(MasterSubTypeBean.class));
        } catch (Exception e) {
            log.error("Error while getting master subtype details for id. Reason: {}", e.getMessage(), e);
        }
        return masterSubTypeDetailsForId;
    }
    
    public List<MasterComponentBean> getComponentMasterDataForAccountId(int accountId) {
        List<MasterComponentBean> masterComponentList = null;
        try {
            String GET_MASTER_COMPONENT_DETAILS_QUERY = "select vc.component_id id,vc.component_name name,vc.is_custom isCustom,vc.component_status status, " +
                    "mc.created_time createdTime,mc.updated_time updatedTime,mc.user_details_id userDetailsId, " +
                    "mc.account_id accountId,mc.description description, mc.discovery_pattern discoveryPattern, vc.component_type_name componentTypeName, " +
                    "vc.component_version_name componentVersionName, vc.component_version_id componentVersionId, " +
                    "vc.component_type_id componentTypeId, vc.common_version_name commonVersionName, " +
                    "vc.common_version_id commonVersionId from view_components vc, mst_component mc " +
                    "where vc.component_id = mc.id and mc.account_id in (1,?)";
            masterComponentList = jdbcTemplate.query(GET_MASTER_COMPONENT_DETAILS_QUERY,
                    new BeanPropertyRowMapper<>(MasterComponentBean.class),accountId);
        } catch (Exception e) {
            log.error("Error in MasterDataDao while getting master component details for id. Reason: {}", e.getMessage(), e);
        }
        return masterComponentList;
    }
   
}

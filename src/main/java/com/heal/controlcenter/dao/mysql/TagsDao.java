package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.TagDetailsBean;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.pojo.TagMappingDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class TagsDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int addTagMappingDetails(TagMappingDetails bean) throws ControlCenterException {
        String query = "insert into tag_mapping (tag_id, object_id, object_ref_table, tag_key, tag_value, created_time, updated_time, account_id, user_details_id) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            log.debug("adding tag mapping details.");
            return jdbcTemplate.update(query, bean.getTagId(), bean.getObjectId(), bean.getObjectRefTable(), bean.getTagKey(), bean.getTagValue(), bean.getCreatedTime(), bean.getUpdatedTime(), bean.getAccountId(), bean.getUserDetailsId());
        } catch (Exception ex) {
            log.error("Error in adding tag mapping details", ex);
            throw new ControlCenterException("Error in adding tag mapping details");
        }
    }

    public TagDetailsBean getTagDetails(String name) throws ControlCenterException {
        String query = "select id,name,tag_type_id tagTypeId,is_predefined isPredefined,ref_table refTable,created_time createdTime,updated_time updatedTime,account_id accountId," +
                "user_details_id userDetailsId,ref_where_column_name refWhereColumnName,ref_select_column_name refSelectColumnName from tag_details where name = ?";
        try{
            log.debug("getting user tag.");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(TagDetailsBean.class), name);
        } catch (Exception ex) {
            log.error("Error in getting tag details.");
            throw new ControlCenterException("Error in getting tag details.");
        }
    }
    public List<TagMappingDetails> getTagMappingDetails(int  accountId) throws ControlCenterException {
        String query = "select id,tag_id tagId,object_id objectId,object_ref_table objectRefTable,tag_key tagKey,tag_value tagValue,created_time createdTime,updated_time updatedTime,account_id accountId,user_details_id userDetailsId from tag_mapping where object_ref_table != 'transaction' and account_id = ?";
        try{
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TagMappingDetails.class), accountId);
        } catch (Exception ex) {
            log.error("Error in getting tag details.");
            return new ArrayList<>();
        }
    }

}

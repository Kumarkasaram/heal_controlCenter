package com.heal.controlcenter.dao.mysql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.heal.controlcenter.beans.ViewComponentAttributesPojo;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ComponentDao {

	  @Autowired
	    JdbcTemplate jdbcTemplate;


	    public List<ViewComponentAttributesPojo> getComponentAttributeDetails() {
	        List<ViewComponentAttributesPojo> viewComponentAttributesList = null;
	        try {
	            String query = "select mcam.mst_common_attributes_id attributeId, mca.attribute_name attributeName, mcam.default_value defaultValue," +
	    	            " mcam.is_mandatory isMandatory, vc.component_id componentId, vc.component_name componentName," +
	    	            " vc.component_type_id componentTypeId, vc.component_type_name componentTypeName, vc.common_version_id commonVersionId," +
	    	            " vc.common_version_name commonVersionName, vc.component_version_id componentVersionId," +
	    	            " vc.component_version_name componentVersionName from view_components vc, mst_component_attribute_mapping mcam," +
	    	            " mst_common_attributes mca where vc.component_id = mcam.mst_component_id and vc.common_version_id = mcam.mst_common_version_id" +
	    	            " and vc.component_type_id = mcam.mst_component_type_id and mcam.mst_common_attributes_id = mca.id and" +
	    	            " vc.component_type_status = 1 and vc.component_status = 1 and vc.is_version_status = 1";
	            viewComponentAttributesList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ViewComponentAttributesPojo.class));
	        } catch (Exception e) {
	            String query = "Error while getting ComponentAttributeDetails";
	            log.error(query + ".Reason: {}", e.getMessage(), e);
	        }
	        return viewComponentAttributesList;
	    }
	
}

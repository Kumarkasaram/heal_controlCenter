package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.ControllerBean;
import com.heal.controlcenter.pojo.GetApplications;
import com.heal.controlcenter.beans.ViewApplicationServiceMappingBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;
@Repository
@Slf4j
public class ControllerDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<ControllerBean> getApplicationsList(Integer accountId) throws ControlCenterException {
        String query = "select id, name, controller_type_id controllerTypeId, identifier identifier, " +
                "status status, user_details_id lastModifiedBy, created_time createdTime, updated_time updatedTime, account_id accountId " +
                "from controller where account_id = " + accountId + " and status = 1 and controller_type_id = 191";
        try {
            log.debug("getting applications list.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ControllerBean.class));
        } catch (Exception e) {
            log.error("Exception encountered while fetching applications list from 'controller' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error occurred while getting applications list.");
        }
    }

    public List<ControllerBean> getServicesList(Integer accountId) throws ControlCenterException {
        String query = "select id, name, identifier, account_id accountId, user_details_id lastModifiedBy, created_time createdTime, " +
                "updated_time updatedTime, controller_type_id controllerTypeId, status from controller " +
                "where account_id = " + accountId + " and controller_type_id = 192";
        try {
            log.debug("getting services list.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ControllerBean.class));
        } catch (EmptyResultDataAccessException e) {
            log.info("Services unmapped to account [{}]", accountId);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Exception encountered while fetching services list from 'controller' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error occurred while getting services list.");
        }
    }

    public List<ViewApplicationServiceMappingBean> getServicesMappedToApplication(Integer accountId, String appIdentifier) throws ControlCenterException {
        String query = "select service_id serviceId, service_name serviceName, service_identifier serviceIdentifier, " +
                "application_id applicationId, application_name applicationName, application_identifier applicationIdentifier " +
                "from view_application_service_mapping " +
                "where account_id = " + accountId + " and application_identifier = '" + appIdentifier + "'";
        try {
            log.debug("getting services linked to application.");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ViewApplicationServiceMappingBean.class));
        } catch (Exception e) {
            log.error("Exception encountered while fetching services linked to application from 'view_application_service_mapping' table for accountId [{}] and applicationIdentifier [{}]. Details: {}, Stack trace: {}", accountId, appIdentifier, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error occurred while getting services linked to application.");
        }
    }

    public int deleteApplicationNotificationMappingWithAppId(int id) throws ControlCenterException {
        String query = "DELETE FROM application_notification_mapping where application_id = ? ";
        try {
            return jdbcTemplate.update(query, id);
        } catch (Exception ex) {
            log.error("Error in deleting application notification mapping with app id.");
            throw new ControlCenterException("Error in deleting application notification mapping with app id.");
        }
    }

    public int deleteUserNotificationMappingWithAppId(int id) throws ControlCenterException {
        String query = "DELETE FROM user_notification_mapping where application_id = ? ";
        try {
            return jdbcTemplate.update(query, id);
        } catch (Exception ex) {
            log.error("Error in deleting user notification mapping with app id.");
            throw new ControlCenterException("Error in deleting user notification mapping with app id.");
        }
    }

    public int deleteApplicationPercentilesWithAppId(int id) throws ControlCenterException {
        String query = "DELETE FROM application_percentiles where application_id = ? ";
        try {
            return jdbcTemplate.update(query, id);
        } catch (Exception ex) {
            log.error("Error in deleting application percentile mapping with app id.");
            throw new ControlCenterException("Error in deleting application percentile mapping with app id.");
        }
    }

    public int deleteControllerWithId(int id) throws ControlCenterException {
        String query = "DELETE FROM controller where id = ?";
        try {
            return jdbcTemplate.update(query, id);
        } catch (Exception ex) {
            log.error("Error in deleting controller with id.");
            throw new ControlCenterException("Error in deleting controller with id.");
        }
    }

    public List<ViewApplicationServiceMappingBean> getServicesMappedToApplication(int accountId, String appId) {
        String query = "select service_id serviceId, service_name serviceName,service_identifier serviceIdentifier from view_application_service_mapping " +
                "where account_id = ? and application_identifier = ?";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ViewApplicationServiceMappingBean.class), accountId, appId);
        } catch(EmptyResultDataAccessException e) {
            log.info("No services mapped to applicationId [{}] and accountId [{}]", appId, accountId);
            return Collections.emptyList();
        }catch (Exception ex) {
            log.error("No services mapped to applicationId [{}] and accountId [{}]. Details: ", appId, accountId, ex);
            return Collections.emptyList();
        }
    }

    public List<GetApplications.ClusterComponentDetails> getHostClusterComponentDetailsForService(String serviceIdentifier) throws ControlCenterException {
        String query = "select vc.id,vc.name,vc.identifier,vci.mst_component_id componentId," +
                " vci.component_name componentName, vci.mst_component_type_id componentTypeId, vci.component_type_name componentTypeName," +
                " vci.mst_component_version_id componentVersionId, vci.component_version_name componentVersionName, vci.common_version_id commonVersionId, " +
                " vci.common_version_name commonVersionName from" +
                " view_cluster_services vc, view_component_instance vci where vc.id = vci.id and vc.mst_component_type_id = 1" +
                " and vc.service_identifier= ? ";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(GetApplications.ClusterComponentDetails.class), serviceIdentifier);
        } catch(EmptyResultDataAccessException e) {
            log.info("No host clusters/instances mapped to serviceIdentifier [{}]", serviceIdentifier);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Error in getting host cluster component details for service. Details: ", ex);
            throw new ControlCenterException("Error in getting host cluster component details for service");
        }
    }

    public List<GetApplications.ClusterComponentDetails> getComponentClusterComponentDetailsForService(String serviceIdentifier) throws ControlCenterException {
        String query = "select vc.id,vc.name,vc.identifier,vci.mst_component_id componentId," +
                " vci.component_name componentName, vci.mst_component_type_id componentTypeId, vci.component_type_name componentTypeName," +
                " vci.mst_component_version_id componentVersionId, vci.component_version_name componentVersionName,vci.common_version_id commonVersionId, " +
                " vci.common_version_name commonVersionName from" +
                " view_cluster_services vc, view_component_instance vci where vc.id = vci.id and vc.mst_component_type_id != 1" +
                " and vc.service_identifier= ?";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(GetApplications.ClusterComponentDetails.class), serviceIdentifier);
        } catch(EmptyResultDataAccessException e) {
            log.info("No component clusters/instances mapped to serviceIdentifier [{}]", serviceIdentifier);
            return Collections.emptyList();
        }  catch (Exception ex) {
            log.error("Error in getting component cluster component details for service. Details: ", ex);
            throw new ControlCenterException("Error in getting component cluster component details for service");
        }
    }

    public List<ControllerBean> getApplicationsList(int accountId) {
        String query = "select id, name, controller_type_id controllerTypeId, identifier, " +
                "status, user_details_id userDetailsId, created_time createdTime, updated_time updatedTime ,account_id accountId " +
                "from controller where account_id = ? and status = 1 and controller_type_id = 191";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ControllerBean.class), accountId);
        } catch (EmptyResultDataAccessException e) {
            log.info("No applications mapped to accountId [{}]", accountId);
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Error in fetching applications for accountId [{}]", accountId, ex);
            return Collections.emptyList();
        }
    }

    public List<ControllerBean> getControllerList(int accountId) throws ControlCenterException {
        String query = "select id appId,name name,controller_type_id controllerTypeId,identifier identifier, plugin_supr_interval pluginSuppressionInterval, plugin_whitelist_status pluginWhitelisted, " +
                "status status,user_details_id createdBy,created_time createdOn, updated_time updatedTime ,account_id accountId " +
                "from controller where account_id = ? and status = 1";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ControllerBean.class), accountId);
        } catch (Exception ex) {
            log.error("Error in fetching controllers", ex);
            throw new ControlCenterException("Error in fetching controllers");
        }
    }

    public ControllerBean getApplicationIdByIdentifier(String identifier) throws ControlCenterException {
        String query = "select id, name, identifier from controller where controller_type_id=191 and identifier = ?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ControllerBean.class), identifier);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            log.error("Error while fetching application details for identifier [{}]", identifier);
            throw new ControlCenterException("Error while fetching application details");
        }
    }

    public ControllerBean getApplicationIdByName(String name) throws ControlCenterException {
        String query = "select id, name, identifier from controller where controller_type_id=191 and name = ?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ControllerBean.class), name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            log.error("Error while fetching application details for name [{}]", name);
            throw new ControlCenterException("Error while fetching application details");
        }
    }

    public int addController(ControllerBean controllerBean) throws ControlCenterException{
        String query = "INSERT INTO controller ( name, identifier, account_id, user_details_id, created_time, updated_time, controller_type_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?)";
        try{
            return jdbcTemplate.update(query, controllerBean.getName(), controllerBean.getIdentifier(), controllerBean.getAccountId(),
                    controllerBean.getLastModifiedBy(), controllerBean.getCreatedTime(), controllerBean.getUpdatedTime(),
                    controllerBean.getControllerTypeId());
        } catch (Exception e) {
            log.error("Error while adding controller [{}]. Details: ", controllerBean, e);
            throw new ControlCenterException("Error while adding controller");
        }
    }
    public List<ControllerBean> getControllerDetailsWithIdentifier(Integer accountId,List<String> controllerIdentifiers) throws ControlCenterException {
        String query = "select distinct c.id appId, c.name name, c.controller_type_id controllerTypeId, c.identifier identifier, c.status status, c.user_details_id createdBy," +
                "c.created_time createdOn, c.updated_time updatedTime, c.account_id accountId " +
                "from controller c where c.account_id = ? and c.identifier in (?) and c.status=1";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ControllerBean.class),accountId,controllerIdentifiers);
        } catch (Exception e) {
            log.error("Exception encountered while getControllerDetailsWithIdentifier list from 'controller' table for accountId [{}]. Details: {}, Stack trace: {}", accountId, e.getMessage(), e.getStackTrace());
            throw new ControlCenterException("Error occurred while getting applications list.");
        }
    }

}

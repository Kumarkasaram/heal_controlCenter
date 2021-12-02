package com.heal.controlcenter.dao.mysql;


import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class UserDao {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    DateTimeUtil dateTimeUtil;

    public boolean adEditStatusKeycloak() throws ControlCenterException {
        String query = "SELECT count(*) from appsonekeycloak.COMPONENT_CONFIG where NAME='editMode' and VALUE='UNSYNCED'";
        try {
            log.debug("getting Ad Edit Status");
            return jdbcTemplate.queryForObject(query, Boolean.class);
        } catch (Exception ex) {
            log.error("Error in Ad Edit Status");
            throw new ControlCenterException("Error in Ad Edit Status");
        }
    }

 /*   public String getSetup() throws ControlCenterException {
        String query = "SELECT value FROM a1_installation_attributes where name = 'SetupType'";
        try {
            log.debug("getting setup");
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception ex) {
            log.error("Error in getting setup");
            throw new ControlCenterException("Error in getting setup");
        }
    }*/

    public UserInfoBean getUserDetails(String userId) throws ControlCenterException {
        String query = "SELECT u.id mysqlId, u.user_identifier id, u.username userName, u.mst_access_profile_id profileId, " +
                "u.mst_role_id roleId, u.status, a.access_details accessDetailsJSON, email_address emailId, contact_number contactNumber, " +
                "u.is_timezone_mychoice isTimezoneMychoice, u.is_notifications_timezone_mychoice isNotificationsTimezoneMychoice " +
                "FROM user_attributes u, user_access_details a " +
                "where u.user_identifier = a.user_identifier and u.user_identifier = ? ";
        try {
            log.debug("getting user details");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserInfoBean.class), userId);
        } catch (Exception ex) {
            log.error("Error in getting user details for user identifier [{}]. Details: ", userId, ex);
            throw new ControlCenterException("Error in getting user details");
        }
    }

    public List<UserDetailsBean> getUsers() throws ControlCenterException {
        String query = "SELECT u.user_identifier id, u.username userName, u.user_details_id updatedBy, a.name userProfile, " +
                "r.name role, u.status, u.updated_time updatedOn FROM user_attributes u, mst_roles r, mst_access_profiles a " +
                "where u.mst_access_profile_id = a.id and u.mst_role_id = r.id and a.mst_role_id = r.id";
        try {
            log.debug("getting users");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(UserDetailsBean.class));
        } catch (Exception ex) {
            log.error("Error in getting users", ex);
            throw new ControlCenterException("Error in getting users");
        }
    }

    public UserBean getUserDetailsFromUsername(String userName) throws ControlCenterException {
        String query = "select first_name firstName, last_name lastName, email, id, username, enabled from appsonekeycloak.USER_ENTITY where username = ?";
        try{
            log.debug("getting user details from username.");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserBean.class), userName);
        } catch (Exception ex) {
            log.error("Error in getting user details from username.");
            throw new ControlCenterException("Error in getting user details from username.");
        }
    }

    public UserProfileBean getUserProfile(int profileId) throws ControlCenterException {
        String query = "select map.id userProfileId, map.name userProfileName, mr.name role " +
                "from mst_access_profiles map, mst_roles mr where mr.id=map.mst_role_id and map.id=?";
        try {
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserProfileBean.class), profileId);
        } catch (Exception ex) {
            log.error("Error in getting user profiles.");
            throw new ControlCenterException("Error in getting user profiles.");
        }
    }

    public UserAccessBean getUserAccessDetails(String userId) throws ControlCenterException{
        String query = "select access_details accessDetailsJson, user_identifier userId from user_access_details where user_identifier= ?";
        try {
            log.debug("getting user access details");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserAccessBean.class), userId);
        } catch (Exception ex) {
            log.error("Error in getting user access details");
            throw new ControlCenterException("Error in getting user access details");
        }
    }

    public String getUsernameFromIdentifier(String userId) throws ControlCenterException {
        String query = "select username from user_attributes where user_identifier= ?";
        try {
            return jdbcTemplate.queryForObject(query, String.class, userId);
        } catch (Exception ex) {
            log.error("Error in fetching username for userId [{}]. Details: ", userId, ex);
            throw new ControlCenterException("Error in fetching username");
        }
    }

    public List<String> getUserIdentifiers() throws ControlCenterException {
        try {
            String USER_IDENTIFIER_QUERY = "select ua.user_identifier from user_attributes ua,user_access_details uad " +
                    "where uad.user_identifier=ua.user_identifier";
            return jdbcTemplate.queryForList(USER_IDENTIFIER_QUERY, String.class);
        } catch (Exception e) {
            log.error("Error while getting user attribute details from DB. Details: ", e);
            throw new ControlCenterException("Error in fetching user attribute details");
        }
    }

    public List<String> getKeycloakUserIdentifiers() throws ControlCenterException {
        try {
            String KEYCLOAK_USER_IDENTIFIER_QUERY = "select id from appsonekeycloak.USER_ENTITY";
            return jdbcTemplate.queryForList(KEYCLOAK_USER_IDENTIFIER_QUERY, String.class);
        } catch (Exception e) {
            log.error("Error while getting keycloak user identifiers from DB. Details: ", e);
            throw new ControlCenterException("Error in fetching keycloak user identifiers.");
        }
    }

    public List<UserBean> getUserDetailsFromKeycloak() throws ControlCenterException {
        try {
            String USER_DETAILS_FROM_KEYCLOAK_QUERY = "select first_name firstName, last_name lastName, email, id, username, enabled " +
                    "from appsonekeycloak.USER_ENTITY";
            return jdbcTemplate.query(USER_DETAILS_FROM_KEYCLOAK_QUERY, new BeanPropertyRowMapper<>(UserBean.class));
        } catch (Exception e) {
            log.error("Error while getting user identifier. Details: ", e);
            throw new ControlCenterException("Error while getting user identifier");
        }
    }

    public UserAttributesBean getRoleProfileInfoForUserId(String userIdentifier) throws ControlCenterException {
        String ROLE_PROFILE_FOR_USER = "select mr.name roleName, mr.id roleId, map.id accessProfileId, map.name accessProfileName " +
                "from mst_roles mr join mst_access_profiles map join user_attributes ua on mr.id=ua.mst_role_id " +
                "and mr.id=map.mst_role_id and map.id=mst_access_profile_id where ua.user_identifier='" + userIdentifier + "'";
        try {
            return jdbcTemplate.queryForObject(ROLE_PROFILE_FOR_USER, new BeanPropertyRowMapper<>(UserAttributesBean.class));
        } catch (Exception e) {
            log.error("Exception encountered while fetching user role and profile. Details: ", e);
            throw new ControlCenterException("Exception encountered while fetching user role and profile");
        }
    }

    public List<String> getUserAccessibleActions(int accessProfileId) throws ControlCenterException {
        String USER_ACCESSIBLE_ACTION = "select vrd.action_identifier from mst_access_profile_mapping mapm join " +
                "view_route_details vrd on mst_big_feature_id=vrd.big_feature_id where " +
                "dashboard_name='ControlCenter' and mst_access_profile_id= ?";
        try {
            return jdbcTemplate.queryForList(USER_ACCESSIBLE_ACTION, String.class, accessProfileId);
        } catch (Exception e) {
            log.error("Exception encountered while fetching user attributes information. Details: ", e);
            throw new ControlCenterException("Exception encountered while fetching user attributes information");
        }
    }

    public String getHealSetup() {
        String defaultSetupType = "Keycloak";
        String setup = defaultSetupType;
        try {
            String query = "SELECT value FROM a1_installation_attributes where name = 'SetupType'";
            setup = jdbcTemplate.queryForObject(query, String.class);
            if (setup == null) {
                return defaultSetupType;
            }
        } catch (Exception e) {
            log.error("Exception while getting setup type: {}", e.getMessage(), e);
        }
        return setup;
    }

    public void deleteUserAttributesAndAccessDetails(String userId) throws ControlCenterException {
        try {
            String deleteUserFromUseridQuery = "delete from  user_attributes where user_identifier = ?";
            jdbcTemplate.update(deleteUserFromUseridQuery, userId);

            String deleteUserFromAccessDetailsQuery = "delete from user_access_details where user_identifier = ?";
            jdbcTemplate.update(deleteUserFromAccessDetailsQuery, userId);
        } catch (Exception e) {
            String ERROR_DELETE_USER_ATTRIBUTES = "Error in deleting user_attributes or user_access_details";
            log.error(ERROR_DELETE_USER_ATTRIBUTES + ". Reason: {}", e.getMessage(), e);
            throw new ControlCenterException(ERROR_DELETE_USER_ATTRIBUTES);
        }
    }

    public List<UserInfoBean> getActiveUsers() throws ControlCenterException {
        List<UserInfoBean> activeUsersList;
        try {
            String GET_ACTIVE_USERS_QUERY = "SELECT  u.user_identifier id, u.created_time createdTime, u.last_login_time lastLoginTime," + "\n" +
                    "  u.is_timezone_mychoice isTimezoneMychoice, u.is_notifications_timezone_mychoice isNotificationsTimezoneMychoice," + "\n" +
                    "  un.FIRST_NAME firstName, un.LAST_NAME lastName, u.username userName, u.email_address emailId" + "\n" +
                    "  FROM user_attributes u, appsonekeycloak.USER_ENTITY un  " + "\n" +
                    "  where u.status = 1 and un.id= u.user_identifier and u.mst_access_profile_id != 1";
            activeUsersList = jdbcTemplate.query(GET_ACTIVE_USERS_QUERY, new BeanPropertyRowMapper<>(UserInfoBean.class));
        } catch (Exception e) {
            log.error("Error while getting active_users. Reason: {}", e.getMessage(), e);
            throw new ControlCenterException("Error while getting active_users");
        }
        return activeUsersList;
    }

    public UserInfoBean getSuperAdmin() {
        UserInfoBean superAdmin = null;
        try {
            String GET_SUPER_ADMIN_QUERY = "SELECT  u.user_identifier id, u.created_time createdTime, u.last_login_time lastLoginTime, " +
                    "u.is_timezone_mychoice isTimezoneMychoice, u.is_notifications_timezone_mychoice isNotificationsTimezoneMychoice, " +
                    "un.FIRST_NAME firstName, un.LAST_NAME lastName, u.username userName, u.email_address emailId " +
                    "FROM user_attributes u, appsonekeycloak.USER_ENTITY un " +
                    "where u.status = 1 and un.id= u.user_identifier and u.mst_access_profile_id = 1";
            superAdmin = jdbcTemplate.queryForObject(GET_SUPER_ADMIN_QUERY, new BeanPropertyRowMapper<>(UserInfoBean.class));
        } catch (Exception e) {
            String ERROR_GETTING_SUPER_ADMIN = "Error while getting super_admin";
            log.error(ERROR_GETTING_SUPER_ADMIN + ".Reason: {}", e.getMessage(), e);
        }
        return superAdmin;
    }

    public void updateUserStatusToInactive(String userIdentifier, String superUserIdentifier) {
        String UPDATE_USER_STATUS_INACTIVE = "UPDATE user_attributes SET u.status = 0, u.user_details_id = " + superUserIdentifier +
                "WHERE u.user_identifier = " + userIdentifier;
        jdbcTemplate.execute(UPDATE_USER_STATUS_INACTIVE);
    }

    public List<UserDetailsBean> getNonSuperUsers() throws ControlCenterException {
        String query = "SELECT u.user_identifier userId, u.username userName, u.user_details_id updatedBy, a.name userProfile, " +
                "r.name role, u.status, u.updated_time updatedOn FROM user_attributes u, mst_roles r, mst_access_profiles a " +
                "where u.mst_access_profile_id = a.id and u.mst_role_id = r.id and a.mst_role_id = r.id and r.name != 'Super Admin'";
        try {
            return jdbcTemplate.query(query, (rs, rowNum) -> {
                UserDetailsBean userDetailsBean = new UserDetailsBean();
                userDetailsBean.setUserId(rs.getString("userId"));
                userDetailsBean.setUpdatedBy(rs.getString("updatedBy"));
                userDetailsBean.setUserName(rs.getString("userName"));
                userDetailsBean.setUpdatedOn(dateTimeUtil.getGMTToEpochTime(rs.getString("updatedOn")));
                userDetailsBean.setStatus(rs.getInt("status"));
                userDetailsBean.setUserProfile(rs.getString("userProfile"));
                userDetailsBean.setRole(rs.getString("role"));
                return userDetailsBean;
            });
        } catch (Exception e) {
            log.error("Exception encountered while fetching users. Reason: ", e);
            throw new ControlCenterException("Exception encountered while fetching users");
        }
    }

    public List<IdBean> getRoles() throws ControlCenterException {
        String query = "select id, name from mst_roles where ui_visible = 1";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(IdBean.class));
        }catch (Exception ex){
            log.info("Error while getting user roles details from DB", ex);
            throw new ControlCenterException("Error in fetching user roles details");
        }
    }

    public List<UserProfileBean> getUserProfiles() throws ControlCenterException {
        String query = "select map.id userProfileId, map.name userProfileName, mr.name role from mst_access_profiles map," +
                "mst_roles mr where mr.id=map.mst_role_id";
        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(UserProfileBean.class));
        } catch (Exception ex) {
            log.info("Error in fetching user profile details from DB", ex);
            throw new ControlCenterException("Error in fetching user profile details");
        }
    }

    public List<String> getAccessProfileMapping(int profileId) throws ControlCenterException {
        String query = "select mb.name from mst_access_profile_mapping ma, mst_big_features mb " +
                "where ma.mst_big_feature_id=mb.id and mb.ui_visible=1 and ma.mst_access_profile_id=?";
        try {
            return jdbcTemplate.queryForList(query, String.class, profileId);
        } catch (Exception ex) {
            log.info("Error in fetching user profiles mapping details from DB", ex);
            throw new ControlCenterException("Error in fetching user profiles mapping");
        }
    }
}

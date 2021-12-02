package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.AccountBean;
import com.heal.controlcenter.beans.UserAccessBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AccountsDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<AccountBean> getAccounts() throws ControlCenterException {
        String query = "select a.identifier identifier, a.id id, a.name name, a.public_key publicKey, a.private_key privateKey, " +
                "a.user_details_id userIdDetails, a.status status, a.updated_time updatedTime, a.created_time createdTime " +
                "from account a where a.status = 1";
        try {
            log.debug("getting accounts list");
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(AccountBean.class));
        } catch (Exception ex) {
            log.error("Error in fetching accounts", ex);
            throw new ControlCenterException("Error in fetching accounts");
        }
    }

    public AccountBean getAccountDetailsForIdentifier(String accountIdentifier) {
        String query = "select a.id as accountId, a.status, a.name, a.identifier, a.user_details_id as userDetailsId " +
                "FROM account a where a.identifier=?";

        try {
            return jdbcTemplate.queryForObject(query, BeanPropertyRowMapper.newInstance(AccountBean.class), accountIdentifier);
        } catch (Exception e) {
            log.error("Exception encountered while fetching accounts information. Details: ", e);
        }

        return null;
    }

    public UserAccessBean fetchUserAccessDetailsUsingIdentifier(String userIdentifier) {
        try {
            String query = "select a.access_details, a.user_identifier from user_access_details a where user_identifier=?";
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(UserAccessBean.class),
                    userIdentifier);
        } catch (DataAccessException e) {
            log.error("Error while fetching user access information for user [{}]. Details: ", userIdentifier, e);
        }

        return null;
    }

    public AccountBean getAccountByIdentifier(String identifier) {
        String query = "select identifier, id, name, public_key publicKey, private_key privateKey, " +
                "user_details_id lastModifiedBy, status, updated_time updatedTime, created_time createdTime " +
                "from account where status = 1 and identifier=?";
        try {
            log.debug("getting account details.");
            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(AccountBean.class), identifier);
        } catch (Exception e) {
            log.error("Error occurred while fetching account details from 'account' table for identifier [{}]. Details: {}, Stack trace: {}", identifier, e.getMessage(), e.getStackTrace());
        }
        return null;
    }
}

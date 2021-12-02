package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.CommandDetailsBean;
import com.heal.controlcenter.exception.ControlCenterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Slf4j
@Repository
@Component
public class CommandDataDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int updateCommandFailureStatus(String physicalAgentIdentifier, String commandJobId, String userId) throws ControlCenterException {
        int updateCommandFailureStatus = 0;
        Date updatedTime = new Date(System.currentTimeMillis());
        try {
            String updateCommandFailureStatusQuery = "UPDATE physical_agent SET is_command_executed=1, user_details_id="+userId+", updated_time="+updatedTime+"\n" +
                    "WHERE last_job_id="+commandJobId+" and identifier="+physicalAgentIdentifier;
            updateCommandFailureStatus = jdbcTemplate.update(updateCommandFailureStatusQuery);
        } catch(Exception e) {
            String ERROR_UPDATE_COMMAND_FAILURE_STATUS = "Error in updating command failure status";
            log.error(ERROR_UPDATE_COMMAND_FAILURE_STATUS + ". Reason: {}", e.getMessage(), e);
            throw new ControlCenterException(ERROR_UPDATE_COMMAND_FAILURE_STATUS);
        }
        return updateCommandFailureStatus;
    }

    public CommandDetailsBean getCommandDetail(int id) {
        CommandDetailsBean commandDetails = null;
        try {
            String GET_COMMAND_DETAILS_QUERY = "select id,name,identifier,command_name commandName,timeout_in_secs timeOutInSecs," +
                    "            output_type_id outputTypeId,command_type_id commandTypeId, user_details_id userDetails," +
                    "            created_time createdTime, updated_time updatedTime, action_id actionId, producer_type_id producerTypeId" +
                    "            from command_details where id="+id;
            commandDetails = jdbcTemplate.queryForObject(GET_COMMAND_DETAILS_QUERY, new BeanPropertyRowMapper<>(CommandDetailsBean.class));
        } catch (Exception e) {
            String ERROR_GETTING_COMMAND_DETAILS = "Error while getting command details";
            log.error(ERROR_GETTING_COMMAND_DETAILS + ".Reason: {}", e.getMessage(), e);
        }
        return commandDetails;
    }
}

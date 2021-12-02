package com.heal.controlcenter.dao.mysql;

import com.heal.controlcenter.beans.CommandTriggerBean;
import com.heal.controlcenter.beans.PhysicalAgentBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AgentStatusDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<PhysicalAgentBean> getPhysicalAgentsForOngoingCommand() {
        List<PhysicalAgentBean> getPhysicalAgentsList = null;
        try {
            String GET_PHYSICAL_AGENTS_QUERY = "select id,last_job_id lastJobId,identifier identifier,user_details_id userDetailsId,is_command_executed lastCommandExecuted " + "\n" +
                    "from physical_agent where is_command_executed=0";
            getPhysicalAgentsList = jdbcTemplate.query(GET_PHYSICAL_AGENTS_QUERY, new BeanPropertyRowMapper<>(PhysicalAgentBean.class));
        } catch (Exception e) {
            String ERROR_GETTING_PHYSICAL_AGENTS = "Error while getting physical agents";
            log.error(ERROR_GETTING_PHYSICAL_AGENTS + ".Reason: {}", e.getMessage(), e);
        }
        return getPhysicalAgentsList;
    }

    public int getAgentCommandTriggerCount(int physicalAgentId) {
        int agentCommandTriggerCount = 0;
        try {
            String GET_AGENT_COMMAND_TRIGGER_COUNT_QUERY = "select count(*) from agent_commands_triggered act where act.physical_agent_id="+physicalAgentId;
            agentCommandTriggerCount = jdbcTemplate.queryForObject(GET_AGENT_COMMAND_TRIGGER_COUNT_QUERY, Integer.class);
        } catch (Exception e) {
            String ERROR_GETTING_AGENT_COMMAND_TRIGGER_STATUS = "Error while getting agent command trigger status";
            log.error(ERROR_GETTING_AGENT_COMMAND_TRIGGER_STATUS + ".Reason: {}", e.getMessage(), e);
        }
        return agentCommandTriggerCount;
    }

    public CommandTriggerBean getAgentCommandTriggerStatus(int physicalAgentId,String commandJobId) {
        CommandTriggerBean agentCommandTriggerStatus = null;
        List<CommandTriggerBean> commandTriggerBeanList = null;
        try {
            String GET_AGENT_COMMAND_TRIGGER_STATUS_QUERY = "select cd.name lastCommandName,mst.name desiredStat,cd.timeout_in_secs timeoutInSecs,acm.trigger_time triggerTime, \n" +
                    "                    (select count(*) from agent_commands_triggered act where act.physical_agent_id="+physicalAgentId+") as noOfCmds,acm.command_id commandId \n" +
                    "                    from agent_commands_triggered acm,command_details cd,mst_sub_type mst where\n" +
                    "                    acm.command_job_id="+commandJobId+" and physical_agent_id="+physicalAgentId+" \n" +
                    "                    and acm.command_id=cd.id and cd.action_id=mst.id";
            commandTriggerBeanList = jdbcTemplate.query(GET_AGENT_COMMAND_TRIGGER_STATUS_QUERY, new BeanPropertyRowMapper<>(CommandTriggerBean.class));
        } catch (Exception e) {
            String ERROR_GETTING_AGENT_COMMAND_TRIGGER_STATUS = "Error while getting agent command trigger status";
            log.error(ERROR_GETTING_AGENT_COMMAND_TRIGGER_STATUS + ".Reason: {}", e.getMessage(), e);
        }
        if(commandTriggerBeanList.size() == 0) {
            return agentCommandTriggerStatus;
        }else{
            return commandTriggerBeanList.get(0);
        }
    }

    public String getAccountIdentifier(int physicalAgentId) {
        String accountIdentifier = null;
        try {
            String GET_ACCOUNT_IDENTIFIER_QUERY = "select identifier from account where id=(select distinct account_id from agent_account_mapping where " +
                    "agent_id in (select id from agent where physical_agent_id="+physicalAgentId+"))";
            accountIdentifier = jdbcTemplate.queryForObject(GET_ACCOUNT_IDENTIFIER_QUERY, String.class);
        } catch (Exception e) {
            String ERROR_GETTING_ACCOUNT_IDENTIFIER = "Error while getting account identifier";
            log.error(ERROR_GETTING_ACCOUNT_IDENTIFIER + ".Reason: {}", e.getMessage(), e);
        }
        return accountIdentifier;
    }
}

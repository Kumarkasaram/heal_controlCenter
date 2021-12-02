package com.heal.controlcenter.scheduler;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.AgentStatusDao;
import com.heal.controlcenter.dao.mysql.CommandDataDao;
import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.exception.CommandException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.repo.RawViolationsRepository;
import com.heal.controlcenter.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CommandTimeoutServiceScheduler {

    @Autowired
    AgentStatusDao agentStatusDao;

    @Autowired
    CommandDataDao commandDataDao;

    @Autowired
    MasterDataDao masterDataDao;

    @Autowired
    DateTimeUtil dateTimeUtil;

    @Autowired
    RawViolationsRepository rawViolationsRepository;

    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    protected void commandTimeoutCheck() {
        log.debug("CommandTimeoutService scheduler running");
        try {
            List<PhysicalAgentBean> commandExecuteDetails = agentStatusDao.getPhysicalAgentsForOngoingCommand();

            for (PhysicalAgentBean c : commandExecuteDetails) {
                CommandTriggerBean commandDetails = agentStatusDao.getAgentCommandTriggerStatus(c.getId(), c.getLastJobId());

                if (Objects.nonNull(commandDetails) && c.getLastCommandExecuted() == 0) {
                    long fromEpochTime = dateTimeUtil.getGMTToEpochTime(String.valueOf(commandDetails.getTriggerTime()));
                    long toEpochTime = dateTimeUtil.getGMTToEpochTime(String.valueOf(new Timestamp(dateTimeUtil.getDateInGMT(System.currentTimeMillis()).getTime())));
                    long diffInMilli = toEpochTime - fromEpochTime;
                    long secondsPassed = (diffInMilli / 1000) / 60;
                    long timeInMinute = commandDetails.getTimeoutInSecs() / 60;

                    if (secondsPassed >= timeInMinute) {
                        commandDataDao.updateCommandFailureStatus(c.getIdentifier(), c.getLastJobId(), c.getUserDetailsId());

                        CommandDetailsBean commandBean = commandDataDao.getCommandDetail(commandDetails.getCommandId());
                        if (commandBean == null) {
                             log.error("Default/Selected command not found for provided commandId [{}] to process the request.", commandDetails.getCommandId());
                            throw new CommandException("Default/Selected command not found to process the request.");
                        }

                        String agentIdentifier = agentStatusDao.getAccountIdentifier(c.getId());

                        Map<String, String> metaData = new HashMap<>();
                        metaData.put("CommandId", commandBean.getIdentifier());
                        metaData.put("AccountId", agentIdentifier);
                        metaData.put("CommandCompleteTime", String.valueOf(toEpochTime));
                        metaData.put("CommandStartTime", String.valueOf(fromEpochTime));
                        metaData.put("ErrorMessageFromPipeline", "Command timed out");
                        metaData.put("ExitCode", "1");
                        ViewTypesBean bean = masterDataDao.getMstSubTypeBySubTypeId(commandBean.getActionId());
                        String desiredStatus = (bean == null) ? null : bean.getTypeName();
                        metaData.put("DesiredState", desiredStatus);
                        try {
                            rawViolationsRepository.insert(agentIdentifier, c.getIdentifier(), commandBean.getName(), c.getLastJobId(), desiredStatus,
                                    commandDetails.getTriggerTime(), metaData);
                        }catch(Exception e){
                            throw new ControlCenterException(e.getCause().getCause().getMessage());
                        }
                        log.info("Command [{}] timed out for agent [{}]. Agent-wise command status updated in CC", c.getLastJobId(), agentIdentifier);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred in CommandTimeoutService. Details: ", e);
        }

    }
}

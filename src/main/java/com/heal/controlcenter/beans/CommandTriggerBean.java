package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTriggerBean {
    private int physicalAgentIdentifier;
    private int commandId;
    private Timestamp triggerTime;
    private String commandJobId;
    private int commandStatus;
    private String desiredStat;
    private String lastCommandName;
    private String userDetailsId;
    private int timeoutInSecs;
    private int noOfCmds;
}

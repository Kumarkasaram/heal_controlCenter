package com.heal.controlcenter.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Getter
@Setter
@UserDefinedType("agent_instance_health")
public class AgentInstanceHealth {
    @Column("account_id")
    private String  account_id;
    @Column("physical_agent_id")
    private String  physical_agent_id;
    @Column("command_id")
    private String command_id;
    @Column("command_job_id")
    private String command_job_id;
    @Column("current_state")
    private String current_state;
    @Column("last_executed_time")
    private Timestamp last_executed_time;
    @Column("metadata")
    private Map<String, String> metadata;
}

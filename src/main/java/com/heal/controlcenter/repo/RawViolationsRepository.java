package com.heal.controlcenter.repo;

import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

@Repository
public class RawViolationsRepository {

    CassandraOperations cassandraTemplate;

    public void insert(String accountId, String physicalAgentId, String commandId, String commandJobId,
                       String currentState, Timestamp lastExecutedTime, Map<String, String> metadata) {

        BuiltStatement builtStatement = QueryBuilder.insertInto("agent_instance_health")
                .value("account_id", accountId)
                .value("physical_agent_id", physicalAgentId)
                .value("command_id", commandId)
                .value("command_job_id", commandJobId)
                .value("current_state", currentState)
                .value("last_executed_time", lastExecutedTime)
                .value("metadata", metadata)
                .value("last_heartbeat_time", new Timestamp(System.currentTimeMillis()));

        cassandraTemplate.insert(builtStatement);
    }
}

package com.heal.controlcenter.dao.cassandra;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.BuiltStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class ConfigureKpiCassandraDao {
    @Autowired
    CassandraOperations cassandraTemplate;

    private static final String COMP_INSTANCE_ID = "comp_instance_id";
    private static final String LAST_COLLECTED_TIME = "last_collected_time";
    private static final String ACCOUNT_ID = "account_id";

    public Long getInstanceHealthMapForAccount(String accountIdentifier,String instanceId)  {
        BuiltStatement builtStatement ;
        Row rows;
        try {
            builtStatement = (QueryBuilder.select().max(LAST_COLLECTED_TIME)
                    .from("comp_instance_health")
                    .where(QueryBuilder.eq(ACCOUNT_ID, accountIdentifier))
                    .and(QueryBuilder.eq(COMP_INSTANCE_ID, instanceId)));

            rows = cassandraTemplate.selectOne(builtStatement.toString(), Row.class);
                Date date = rows.getTimestamp(0);
            if(date != null) {
                return date.getTime();
            }

        } catch (DataAccessException e) {
            log.error("Error while fetching  Detail getSignal() of AccountSignalDao class ", e);
        }
        return 0L;
    }
}

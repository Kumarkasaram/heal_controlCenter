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
public class PhysicalAgentBean {

    private int id;
    private int lastCommandExecuted;
    private Integer lastStatusId;
    private String identifier;
    private String userDetailsId;
    private String lastJobId;
    private Timestamp createdTime;
    private Timestamp updatedTime;

}

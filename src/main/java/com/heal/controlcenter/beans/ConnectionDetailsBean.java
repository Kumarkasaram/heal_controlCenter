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
public class ConnectionDetailsBean {

    private int id;
    private int sourceId;
    private String sourceRefObject;
    private int destinationId;
    private String destinationRefObject;
    private Timestamp createdTime;
    private Timestamp updatedTime;
    private int accountId;
    private String userDetailsId;
    private int isDiscovery;

}

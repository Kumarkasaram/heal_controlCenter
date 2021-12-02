package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationPercentilesBean implements Serializable {

    private int id;
    private int accountId;
    private int applicationId;
    private String displayName;
    private int percentileValue;
    private String createdTime;
    private String updatedTime;
    private String userDetailsId;

}

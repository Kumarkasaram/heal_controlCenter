package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsBean {

    private int typeId;
    private String typeName;
    private int durationInMin;
    private Map<String,Integer> properties = new HashMap<>();
    private String updatedTime;
    private String createdTime;
    private String lastModifiedBy;
    private int accountId;

}

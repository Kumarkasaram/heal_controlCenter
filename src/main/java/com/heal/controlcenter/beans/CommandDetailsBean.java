package com.heal.controlcenter.beans;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDetailsBean {

    private int id;
    @NonNull
    private String name;
    private String identifier;
    @NonNull
    private String commandName;
    @NonNull
    private int timeOutInSecs;
    @NonNull
    private int outputTypeId;
    @NonNull
    private int commandTypeId;
    @NonNull
    private int actionId;
    private String userDetails;
    private Timestamp createdTime;
    private Timestamp updatedTime;
    private int isDefault;
    private int producerTypeId;

    private List<CommandDetailArgumentBean> commandArguments;

}

package com.heal.controlcenter.beans;

import com.heal.controlcenter.dao.mysql.MasterDataDao;
import com.heal.controlcenter.util.DateTimeUtil;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDetailArgumentBean {

    @Autowired
    MasterDataDao masterDataDao;
    private int id;
    private int commandId;
    @NonNull
    private String argumentKey;
    @NonNull
    private String argumentValue;
    @NonNull
    private String defaultValue;
    private int argumentTypeId;
    private int argumentValueTypeId;
    private String userDetails;
    private Timestamp createdTime;
    private Timestamp updatedTime;
    private boolean placeHolder = false;

    @Autowired
    DateTimeUtil dateTimeUtil;
}

package com.heal.controlcenter.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSettingsPojo {

    private int typeId;
    private String typeName;
    private float durationInMin;

    private Map<String, String> error = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(NotificationSettingsPojo.class);

    @Override
    public int hashCode() {
        return 1;
    }

    public void validate() {
        if (typeName.isEmpty())
            error.put("typeName", "typeName is empty");

        if (typeId==0)
            error.put("typeId", "typeId is empty");

        if (durationInMin==0)
            error.put("durationInMin", "durationInMin is empty");

        if (!((durationInMin - (int)durationInMin) == 0))
            error.put("durationInMin", "Decimal values not allowed for durationInMin");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NotificationSettingsPojo))
            return false;

        NotificationSettingsPojo setting = (NotificationSettingsPojo) obj;
        return (setting.typeId > 0 && setting.typeId == typeId) && (setting.durationInMin > 0 && setting.durationInMin == durationInMin);
    }
}

package com.heal.controlcenter.pojo;

import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tags {
    private int tagId;
    private String name;
    private String value;
    private String identifier;
    private String subTypeName;
    private String layer;

    public void validate() throws ControlCenterException {
        if (StringUtils.isEmpty(this.identifier)) throw new ControlCenterException("Tag identifier can not be null or empty.");
        if (StringUtils.isEmpty(this.name)) throw new ControlCenterException("Tag name can not be null or empty.");
        if (this.value != null && this.value.length() < 1) throw new ControlCenterException("Tag value can not be or empty.");
        if (StringUtils.isEmpty(this.subTypeName)) throw new ControlCenterException("Tag subTypeName can not be null or empty.");
    }
}

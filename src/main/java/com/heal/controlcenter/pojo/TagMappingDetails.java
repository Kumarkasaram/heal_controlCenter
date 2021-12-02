package com.heal.controlcenter.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagMappingDetails {

    int id;
    int tagId;
    int objectId;
    String objectRefTable;
    String tagKey;
    String tagValue;
    int accountId;
    String userDetailsId;
    String createdTime;
    String updatedTime;

    @Override
    public String toString() {
        return "TagMappingDetails{" +
                "tagId=" + tagId +
                ", objectId=" + objectId +
                ", objectRefTable='" + objectRefTable + '\'' +
                ", tagKey='" + tagKey + '\'' +
                ", tagValue='" + tagValue + '\'' +
                ", accountId=" + accountId +
                '}';
    }
}


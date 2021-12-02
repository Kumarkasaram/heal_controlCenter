package com.heal.controlcenter.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UtilityBean<T> {

    T pojoObject;
    String accountIdentifier;
    String authToken;
    String userId;
    AccountBean account;

}

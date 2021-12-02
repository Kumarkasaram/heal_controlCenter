package com.heal.controlcenter.pojo;

import com.heal.controlcenter.beans.AccountBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountPojo {

    private String userId;
    private AccountBean account;

}

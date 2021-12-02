package com.heal.controlcenter.beans;

import lombok.*;

@Data
@Builder
@ToString()
@NoArgsConstructor
@AllArgsConstructor
public class AccountBean {

    private int id;
    @NonNull
    private String name;
    private String createdTime;
    private String updatedTime;
    private int status;
    @ToString.Exclude
    private String privateKey;
    @ToString.Exclude
    private String publicKey;
    @NonNull
    private String lastModifiedBy;
    @NonNull
    private String identifier;
    private String UserIdDetails;

}

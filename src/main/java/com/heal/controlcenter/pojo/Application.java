package com.heal.controlcenter.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Application {
    
    private static final String DEFAULT_TIMEZONE = "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi";

    @Max(128)
    @Min(1)
    @NonNull
    @NotEmpty (message = "Invalid Name : Name cannot be NULL or empty. It should have 1 - 128 characters.")
    @NotBlank (message = "Invalid Name : Name cannot be null or empty. It should have 1 - 128 characters.")
    private String name;

    @Max(128)
    private String identifier;

    private String timezone = DEFAULT_TIMEZONE;

    @NonNull
    @NotBlank (message = "Account identifier cannot be NULL or empty")
    @NotEmpty (message = "Account identifier cannot be NULL or empty")
    @JsonProperty(required = true)
    private String accountIdentifier;

    private String userId;
}

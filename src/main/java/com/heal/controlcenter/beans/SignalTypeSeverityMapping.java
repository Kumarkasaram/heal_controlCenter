package com.heal.controlcenter.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalTypeSeverityMapping {

    private int signalTypeId;
    private int signalSeverityId;
}

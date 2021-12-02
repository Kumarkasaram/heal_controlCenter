package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.GetSMSConfigurationsBL;
import com.heal.controlcenter.businesslogic.PutSMSConfigurationsBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.pojo.SMSDetailsPojo;
import com.heal.controlcenter.util.JsonFileParser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Configuration
@RestController
public class SMSConfigurationsController {

    @Autowired
    GetSMSConfigurationsBL getSMSConfigurationsBL;
    @Autowired
    PutSMSConfigurationsBL putSMSConfigurationsBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetches SMS configurations of the user.", response = SMSDetailsPojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "SMS configurations fetched successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while fetching SMS configurations."),
            @ApiResponse(code = 400, message = "Error occurred while fetching SMS configurations.")})
    @RequestMapping(value = "/accounts/{identifier}/sms-configurations", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<SMSDetailsPojo>> getSmsConfigurations(@RequestHeader("Authorization") String authorization,
                                                                             @PathVariable("identifier") String accountIdentifier)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : getSmsConfigurations");

        UtilityBean<Object> smsDetailsUtilityBean = getSMSConfigurationsBL.clientValidation(null, authorization, accountIdentifier);
        Integer accountId = getSMSConfigurationsBL.serverValidation(smsDetailsUtilityBean);
        SMSDetailsPojo data = getSMSConfigurationsBL.process(accountId);

        ResponsePojo<SMSDetailsPojo> responsePojo = new ResponsePojo<>("SMS configurations fetched successfully.", data, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }

    @ApiOperation(value = "Updates SMS configurations of the user.", response = ResponsePojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "SMS configurations updated successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while updating SMS configurations."),
            @ApiResponse(code = 400, message = "Error occurred while updating SMS configurations.")})
    @RequestMapping(value = "/accounts/{identifier}/sms-configurations", method = RequestMethod.PUT)
    public ResponseEntity<ResponsePojo<Object>> putSmsConfigurations(@RequestHeader("Authorization") String authorization,
                                                                     @PathVariable("identifier") String accountIdentifier,
                                                                     @Validated @RequestBody SMSDetailsPojo body)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : putSmsConfigurations");

        UtilityBean<SMSDetailsPojo> smsDetailsUtilityBean = putSMSConfigurationsBL.clientValidation(body, authorization, accountIdentifier);
        putSMSConfigurationsBL.serverValidation(smsDetailsUtilityBean);
        putSMSConfigurationsBL.process(smsDetailsUtilityBean);

        ResponsePojo<Object> responsePojo = new ResponsePojo<>("SMS configurations updated successfully.", null, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

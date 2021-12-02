package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.GetEmailConfigurationsBL;
import com.heal.controlcenter.businesslogic.PutEmailConfigurationsBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.pojo.SMTPDetailsPojo;
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
public class EmailConfigurationsController {

    @Autowired
    GetEmailConfigurationsBL getEmailConfigurationsBL;
    @Autowired
    PutEmailConfigurationsBL putEmailConfigurationsBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetches Email configurations of the user.", response = SMTPDetailsPojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Email configurations fetched successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while fetching Email configurations."),
            @ApiResponse(code = 400, message = "Error occurred while fetching Email configurations.")})
    @RequestMapping(value = "/accounts/{identifier}/email-configurations", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<SMTPDetailsPojo>> getEmailConfigurations(@RequestHeader("Authorization") String authorization,
                                                                                @PathVariable("identifier") String accountIdentifier)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : getEmailConfigurations");

        UtilityBean<Object> emailUtilityBean = getEmailConfigurationsBL.clientValidation(authorization, accountIdentifier);
        Integer accountId = getEmailConfigurationsBL.serverValidation(emailUtilityBean);
        SMTPDetailsPojo data = getEmailConfigurationsBL.process(accountId);

        ResponsePojo<SMTPDetailsPojo> responsePojo = new ResponsePojo<>("Email configurations fetched successfully.", data, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }

    @ApiOperation(value = "Updates Email configurations of the user.", response = ResponsePojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Email configurations updated successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while updating Email configurations."),
            @ApiResponse(code = 400, message = "Error occurred while updating Email configurations.")})
    @RequestMapping(value = "/accounts/{identifier}/email-configurations", method = RequestMethod.PUT)
    public ResponseEntity<ResponsePojo<Object>> putEmailConfigurations(@RequestHeader("Authorization") String authorization,
                                                                       @PathVariable("identifier") String accountIdentifier,
                                                                       @Validated @RequestBody SMTPDetailsPojo body)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : putEmailConfigurations");

        UtilityBean<SMTPDetailsPojo> smtpUtilityBean = putEmailConfigurationsBL.clientValidation(body, authorization, accountIdentifier);
        putEmailConfigurationsBL.serverValidation(smtpUtilityBean);
        putEmailConfigurationsBL.process(smtpUtilityBean);

        ResponsePojo<Object> responsePojo = new ResponsePojo<>("Email configurations updated successfully.", null, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

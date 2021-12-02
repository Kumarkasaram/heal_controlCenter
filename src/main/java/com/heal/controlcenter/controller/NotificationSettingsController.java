package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.NotificationSettingsBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.GetNotificationSettingsBL;
import com.heal.controlcenter.businesslogic.PutNotificationSettingsBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.NotificationSettingsPojo;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.pojo.UserAccountPojo;
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

import java.util.List;

@Slf4j
@Configuration
@RestController
public class NotificationSettingsController {

    @Autowired
    GetNotificationSettingsBL getNotificationSettingsBL;
    @Autowired
    PutNotificationSettingsBL putNotificationSettingsBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetches notification settings of the user.", response = NotificationSettingsBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Notification settings fetched successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while fetching notification settings."),
            @ApiResponse(code = 400, message = "Error occurred while fetching notification settings.")})
    @RequestMapping(value = "/accounts/{identifier}/notification-settings", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<List<NotificationSettingsBean>>> getNotificationSettings(@RequestHeader("Authorization") String authorization,
                                                                                                @PathVariable("identifier") String accountIdentifier)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : getNotificationSettings");

        UtilityBean<Object> utilityBean = getNotificationSettingsBL.clientValidation(authorization, accountIdentifier);
        UserAccountPojo user = getNotificationSettingsBL.serverValidation(utilityBean);
        List<NotificationSettingsBean> data = getNotificationSettingsBL.process(user);

        ResponsePojo<List<NotificationSettingsBean>> responsePojo = new ResponsePojo<>("Notification settings fetched successfully.", data, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }


    @ApiOperation(value = "Updates notification settings of the user.", response = ResponsePojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Notification settings updated successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while updating notification settings."),
            @ApiResponse(code = 400, message = "Error occurred while updating Notification Settings.")})
    @RequestMapping(value = "/accounts/{identifier}/notification-settings", method = RequestMethod.PUT)
    public ResponseEntity<ResponsePojo<Object>> putNotificationSettings(@RequestHeader("Authorization") String authorization,
                                                                        @PathVariable("identifier") String accountIdentifier,
                                                                        @Validated @RequestBody List<NotificationSettingsPojo> body)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : putNotificationSettings");

        UtilityBean<List<NotificationSettingsPojo>> utilityBean = putNotificationSettingsBL.clientValidation(body, authorization, accountIdentifier);
        UtilityBean<List<NotificationSettingsPojo>> settingsBean = putNotificationSettingsBL.serverValidation(utilityBean);
        putNotificationSettingsBL.process(settingsBean);

        ResponsePojo<Object> responsePojo = new ResponsePojo<>("Notification settings updated successfully.", null, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

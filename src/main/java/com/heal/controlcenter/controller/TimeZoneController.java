package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.TimezoneBean;
import com.heal.controlcenter.businesslogic.TimeZoneBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.util.JsonFileParser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@RestController
@Slf4j
public class TimeZoneController {

    @Autowired
    TimeZoneBL timeZoneBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Retrieve timezones", response = TimezoneBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Timezones fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching timezones"),
            @ApiResponse(code = 400, message = "Error in fetching timezones")})
    @RequestMapping(value = "/timezones", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<List<TimezoneBean>>> getAllTimezones(@RequestHeader(value = "Authorization") String authorization)
            throws ClientException, DataProcessingException {
        timeZoneBL.clientValidation(null, authorization);
        List<TimezoneBean> listOfTimeZones = timeZoneBL.process("Timezones");

        ResponsePojo<List<TimezoneBean>> responsePojo = new ResponsePojo<>("Timezones fetched successfully", listOfTimeZones, HttpStatus.OK);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

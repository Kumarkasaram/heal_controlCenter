package com.heal.controlcenter.controller;

import com.heal.controlcenter.businesslogic.KeycloakDetailsBL;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Configuration
public class KeycloakSettingsController {
    @Autowired
    private KeycloakDetailsBL keycloakDetailsBL;

    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Retrieve Account List", response = ResponsePojo.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Keycloak details fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching keycloak details"),
            @ApiResponse(code = 400, message = "Error in fetching keycloak details")})
    @RequestMapping(value = "/keycloak-settings", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<Map<String, Object>>> getKeyCloakSettings() throws DataProcessingException {
            Map<String, Object> data = keycloakDetailsBL.process("Keycloak settings");
            ResponsePojo<Map<String, Object>> responsePojo = new ResponsePojo<>("Keycloak details fetched successfully", data, HttpStatus.OK);
            return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

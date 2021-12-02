package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.InstallationAttributeBean;
import com.heal.controlcenter.businesslogic.InstallationAttributeBL;
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

@Slf4j
@RestController
@Configuration
public class InstallationAttributesController {

    @Autowired
    InstallationAttributeBL installationAttributeBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Retrieve list of installation attribute details", response = InstallationAttributeBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Installation details fetching successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching installation attribute details"),
            @ApiResponse(code = 400, message = "Error in fetching installation attribute details")})
    @RequestMapping(value = "/installation-attributes", method = RequestMethod.GET)
    public ResponseEntity<Object> getInstallationAttributes(@RequestHeader(value = "Authorization", required = false) String authorization) throws ClientException, DataProcessingException {
        installationAttributeBL.clientValidation(null, authorization);
        List<InstallationAttributeBean> listOfInstallationAttributes = installationAttributeBL.process("Installation attributes");
        ResponsePojo<List<InstallationAttributeBean>> responsePojo = new ResponsePojo<>("Installation details fetching successfully", listOfInstallationAttributes, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

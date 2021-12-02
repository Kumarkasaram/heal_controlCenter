package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.IdBean;
import com.heal.controlcenter.businesslogic.UserRoleBL;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author Sourav Suman - 18-10-2021
 */

@Slf4j
@Controller
@Configuration
public class RolesAndProfileController {

    @Autowired
    UserRoleBL userRoleBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Retrieve user role detail", response = IdBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved data"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching user roles"),
            @ApiResponse(code = 400, message = "Error in fetching user roles")})
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<List<IdBean>>> getUserRoles(@RequestHeader(value = "Authorization", required = false) String authorization)
            throws ClientException, DataProcessingException {
        userRoleBL.clientValidation(null, authorization);
        List<IdBean> listOfUserRoles = userRoleBL.process("User roles");
        ResponsePojo<List<IdBean>> responsePojo = new ResponsePojo<>("User roles fetched successfully", listOfUserRoles, HttpStatus.OK);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.businesslogic.UserAccessibleActionBL;
import com.heal.controlcenter.businesslogic.UserDetailsBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
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

@RestController
@Configuration
@Slf4j
public class UserAccessController {

    @Autowired
    UserAccessibleActionBL userAccessibleActionBL;
    @Autowired
    UserDetailsBL userDetailsBL;

    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetch user access information", response = UserAccessibleActions.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User access information successfully fetched."),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 400, message = "Invalid Request")})
    @RequestMapping(value = "/users/access-info", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<UserAccessibleActions>> getUserAccessInformation(@RequestHeader(value = "Authorization", required = false) String authorization)
            throws ClientException, ServerException, DataProcessingException {

        UtilityBean<String> userId = userAccessibleActionBL.clientValidation(null, authorization);
        UserAttributesBean userAttributesBean = userAccessibleActionBL.serverValidation(userId);
        UserAccessibleActions userActionsDetails = userAccessibleActionBL.process(userAttributesBean);

        ResponsePojo<UserAccessibleActions> response = new ResponsePojo<>("User access information fetched successfully", userActionsDetails, HttpStatus.OK);

        return new ResponseEntity<>(response, headersParser.loadHeaderConfiguration(), response.getResponseStatus());
    }

    @ApiOperation(value = "Fetch users information", response = UserDetailsBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User information successfully fetched."),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 400, message = "Invalid Request")})
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<Object> getUsers(@RequestHeader(value = "Authorization", required = false) String authorization) throws ClientException, DataProcessingException {

        userDetailsBL.clientValidation(null, authorization);
        List<UserDetailsBean> userDetailsBeanList = userDetailsBL.process("Users detail");

        ResponsePojo<List<UserDetailsBean>> responsePojo = new ResponsePojo<>("Users detail fetch successfully", userDetailsBeanList, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UserProfileBean;
import com.heal.controlcenter.businesslogic.UserProfilesBL;
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
 * @author Sourav Suman - 19-10-2021
 */

@Slf4j
@Configuration
@Controller
public class UserProfilesController {

    @Autowired
    UserProfilesBL userProfilesBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetch user profiles", response = UserProfileBean.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User profile information successfully fetched."),
            @ApiResponse(code = 500, message = "Exception while fetching user profiles information"),
            @ApiResponse(code = 400, message = "Error in fetching user profiles information")})
    @RequestMapping(value = "/user-profiles", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<List<UserProfileBean>>> getUserProfiles(@RequestHeader(value = "Authorization", required = false) String authorization)
            throws ClientException, DataProcessingException {

        userProfilesBL.clientValidation(null, authorization);
        List<UserProfileBean> listOfUserProfiles = userProfilesBL.process("User profiles");

        ResponsePojo<List<UserProfileBean>> responsePojo = new ResponsePojo<>("Timezones fetched successfully", listOfUserProfiles, HttpStatus.OK);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

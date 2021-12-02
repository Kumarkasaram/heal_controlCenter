package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.businesslogic.GetConnectionsBL;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.GetConnectionPojo;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Configuration
@RestController
public class ConnectionsController {

    @Autowired
    GetConnectionsBL getConnectionsBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Fetches connections related to the account.", response = GetConnectionPojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Connections fetched successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while fetching connections."),
            @ApiResponse(code = 400, message = "Error occurred while fetching connections.")})
    @RequestMapping(value = "/accounts/{identifier}/connections", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<List<GetConnectionPojo>>> getConnections(@RequestHeader("Authorization") String authorization,
                                                                                @PathVariable("identifier") String accountIdentifier)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : getConnections");

        UtilityBean<Object> utilityBean = getConnectionsBL.clientValidation(authorization, accountIdentifier);
        Integer accountId = getConnectionsBL.serverValidation(utilityBean);
        List<GetConnectionPojo> data = getConnectionsBL.process(accountId);

        ResponsePojo<List<GetConnectionPojo>> responsePojo = new ResponsePojo<>("Connections fetched successfully.", data, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.businesslogic.GetEntityCountBL;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Configuration
@RestController
@Slf4j
public class EntityCountController {

    @Autowired
    GetEntityCountBL getEntityCountBL;
    @Autowired
    JsonFileParser headersParser;

    @ApiOperation(value = "Retrieves entity count information.", response = Map.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Entity count fetched successfully."),
            @ApiResponse(code = 500, message = "Exception encountered while fetching entity count details."),
            @ApiResponse(code = 400, message = "Error occurred while fetching entity count details.")})
    @RequestMapping(value ="/accounts/{identifier}/entity-count", method = RequestMethod.GET)
    public ResponseEntity<ResponsePojo<Map<String, Object>>> getEntityCount(@RequestHeader("Authorization") String authorization,
                                                                            @RequestParam("type") String typeName,
                                                                            @PathVariable("identifier") String accountIdentifier)
            throws ClientException, ServerException, DataProcessingException {
        log.trace("Method Invoked : getEntityCount");

        UtilityBean<InstancesBean> utilityBean = getEntityCountBL.clientValidation(null, authorization, accountIdentifier, typeName);
        InstancesBean bean = getEntityCountBL.serverValidation(utilityBean);
        Map<String, Object> data = getEntityCountBL.process(bean);

        ResponsePojo<Map<String, Object>> responsePojo = new ResponsePojo<>("Entity count fetched successfully.", data, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }
}

package com.heal.controlcenter.controller;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.businesslogic.*;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.Application;
import com.heal.controlcenter.pojo.GetApplications;
import com.heal.controlcenter.pojo.IdPojo;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.pojo.UserAccessInfo;
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

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@Configuration
public class ApplicationsController {

    @Autowired
    GetApplicationsBL getApplicationsBL;
    @Autowired
    AddApplicationsBL addApplicationsBL;
    @Autowired
    DeleteApplicationsBL deleteApplicationsBL;
    @Autowired
    JsonFileParser headersParser;

    @Autowired
    GetAgentTypeAtAccLvlBL getAgentTypeAtAccLvlBL;
    @Autowired
    GetComponentAttributesBL getComponentAttributesBL;

    @ApiOperation(value = "Retrieve list of applications", response = GetApplications.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Applications fetching successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching applications"),
            @ApiResponse(code = 400, message = "Error in fetching applications")})
    @RequestMapping(value = "accounts/{identifier}/applications", method = RequestMethod.GET)
    public ResponseEntity<Object> applications(@RequestHeader(value = "Authorization") String authorization, @PathVariable(value = "identifier") String accountIdentifier,
                                               @RequestParam(value = "clusterDataRequired", required = false, defaultValue = "true") String clusterDataRequired)
            throws ClientException, DataProcessingException, ServerException, ControlCenterException {

        UtilityBean<String> applicationBean = getApplicationsBL.clientValidation(authorization, accountIdentifier, clusterDataRequired);
        UserAccessInfo userAccessInfo = getApplicationsBL.serverValidation(applicationBean);
        List<GetApplications> listOfApplications = getApplicationsBL.process(userAccessInfo);

        ResponsePojo<List<GetApplications>> responsePojo = new ResponsePojo<>("Applications fetching successfully",
                listOfApplications, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }

    @ApiOperation(value = "Add list of applications", response = IdPojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Applications added successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while adding applications"),
            @ApiResponse(code = 400, message = "Error in adding applications")})
    @RequestMapping(value = "accounts/{identifier}/applications", method = RequestMethod.POST)
    public ResponseEntity<Object> addApplications(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                  @PathVariable(value = "identifier", required = false) String accountIdentifier,
                                                  @Valid @RequestBody Application body)
            throws ClientException, ServerException, DataProcessingException {

        UtilityBean<Application> applicationBean = addApplicationsBL.clientValidation(body, authorization, accountIdentifier);
        ApplicationBean bean = addApplicationsBL.serverValidation(applicationBean);
        IdPojo idPojo = addApplicationsBL.process(bean);

        ResponsePojo<IdPojo> responsePojo = new ResponsePojo<>("Applications added successfully", idPojo, HttpStatus.OK);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(responsePojo);
    }

    @ApiOperation(value = "Delete list of applications", response = IdPojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Applications removed successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while removing applications"),
            @ApiResponse(code = 400, message = "Error in removing applications")})
    @RequestMapping(method = RequestMethod.DELETE, value = "accounts/{identifier}/applications")
    public ResponseEntity<?> deleteApplications(@RequestHeader(value = "Authorization") String authorization,
                                                @PathVariable(value = "identifier") String accountIdentifier,
                                                @RequestParam(value = "appIdentifiers") String[] appIdentifiers)
            throws DataProcessingException, ClientException, ServerException {

        UtilityBean<List<String>> applicationList = deleteApplicationsBL.clientValidation(appIdentifiers, authorization, accountIdentifier);
        List<ControllerBean> controllerBeanList = deleteApplicationsBL.serverValidation(applicationList);
        deleteApplicationsBL.process(controllerBeanList);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body("Applications removed successfully");
    }

    @ApiOperation(value = "agentType list of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "AgentType list  fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething  AgentType list"),
            @ApiResponse(code = 400, message = "Error in fething  AgentType list ")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/agent-types")
    public ResponseEntity<Object> getAgentTypesAtAccLvl(@RequestHeader(value = "Authorization") String authorization,
                                                   @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {

        UtilityBean<Object> applicationList = getAgentTypeAtAccLvlBL.clientValidation(null,authorization, accountIdentifier);
        Integer controllerBeanList = getAgentTypeAtAccLvlBL.serverValidation(applicationList);
        List<AgentTypePojo> agentTypeList = getAgentTypeAtAccLvlBL.process(controllerBeanList);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(agentTypeList);
    }

    @ApiOperation(value = "componentAttributesMappingList  of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "componentAttributesMappingList   fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething componentAttributesMappingList"),
            @ApiResponse(code = 400, message = "Error in fething componentAttributesMappingList ")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/component-attributes ")
    public ResponseEntity<Object> getComponentAttributes(@RequestHeader(value = "Authorization") String authorization,
                                                    @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {

        UtilityBean<Object> applicationList = getComponentAttributesBL.clientValidation(null,authorization, accountIdentifier);
        Integer controllerBeanList = getComponentAttributesBL.serverValidation(applicationList);
        List<ComponentAttributesMapping> componentAttributesMappingList = getComponentAttributesBL.process(controllerBeanList);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(componentAttributesMappingList);
    }
}

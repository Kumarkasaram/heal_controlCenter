package com.heal.controlcenter.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.businesslogic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.Application;
import com.heal.controlcenter.pojo.GetApplications;
import com.heal.controlcenter.pojo.IdPojo;
import com.heal.controlcenter.pojo.ResponsePojo;
import com.heal.controlcenter.pojo.UserAccessInfo;
import com.heal.controlcenter.util.Constants;
import com.heal.controlcenter.util.JsonFileParser;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


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
    @Autowired
    GetComponentDetailsBL getComponentDetailsBL;
    @Autowired
    GetAvailabilityCategoriesBL getAvailabilityCategoriesBL;
    @Autowired
    GetHealthOfInstancesBL getHealthOfInstancesBL;
    @Autowired
    GetAuditTrailBL getAuditTrailBL;
    

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
        Integer accountId = getAgentTypeAtAccLvlBL.serverValidation(applicationList);
        List<AgentTypePojo> agentTypeList = getAgentTypeAtAccLvlBL.process(accountId);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(agentTypeList);
    }

    @ApiOperation(value = "componentAttributesMappingList  of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "componentAttributesMappingList   fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething componentAttributesMappingList"),
            @ApiResponse(code = 400, message = "Error in fething componentAttributesMappingList ")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/component-attributes")
    public ResponseEntity<Object> getComponentAttributes(@RequestHeader(value = "Authorization") String authorization,
                                                    @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {

        UtilityBean<Object> applicationList = getComponentAttributesBL.clientValidation(null,authorization, accountIdentifier);
        Integer accountId = getComponentAttributesBL.serverValidation(applicationList);
        List<ComponentAttributesMapping> componentAttributesMappingList = getComponentAttributesBL.process(accountId);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(componentAttributesMappingList);
    }
    
    @ApiOperation(value = "Component detail of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Component detail  fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething  component-details list"),
            @ApiResponse(code = 400, message = "Error in fething  Component list ")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/component-details")
    public ResponseEntity<Object> getComponentDetails(@RequestHeader(value = "Authorization") String authorization,
                                                   @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {
        UtilityBean<Object> applicationList = getComponentDetailsBL.clientValidation(null,authorization, accountIdentifier);
        Integer accountId = getComponentDetailsBL.serverValidation(applicationList);
        List<ComponentDetails> componentDetailList = getComponentDetailsBL.process(accountId);

        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(componentDetailList);
    }

    @ApiOperation(value = "availabile Categories of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "availabile Categories  fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething  availabile Categories"),
            @ApiResponse(code = 400, message = "Error in fething  availabile Categories")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/availabilityCategories")
    public ResponseEntity<Object> availabilityCategories(@RequestHeader(value = "Authorization") String authorization,
                                                   @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {
        UtilityBean<Object> applicationList = getAvailabilityCategoriesBL.clientValidation(null,authorization, accountIdentifier);
        Integer accountId = getAvailabilityCategoriesBL.serverValidation(applicationList);
        List<GetCategory> categoryList = getAvailabilityCategoriesBL.process(accountId);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(categoryList);
    }

    @ApiOperation(value = "fetch HealthOfInstances of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "HealthOfInstances  fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fething  HealthOfInstances"),
            @ApiResponse(code = 400, message = "Error in fetching HealthOfInstances")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/health_instances")
    public ResponseEntity<Object> getHealthOfInstances(@RequestHeader(value = "Authorization") String authorization,
                                                         @PathVariable(value = "identifier") String accountIdentifier)
            throws DataProcessingException, ClientException, ServerException {
        UtilityBean<Object> applicationList = getHealthOfInstancesBL.clientValidation(null,authorization, accountIdentifier);
        UtilityBean<Object> utilityBean = getHealthOfInstancesBL.serverValidation(applicationList);
        List<InstanceHealthDetails> instanceHealthDetailsList = getHealthOfInstancesBL.process(utilityBean);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(instanceHealthDetailsList);
    }

    @ApiOperation(value = "fetch audit trail of applications", response = AgentTypePojo.class, responseContainer = "List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = " audit trail  fetched successfully"),
            @ApiResponse(code = 500, message = "Exception encountered while fetching   audit trail"),
            @ApiResponse(code = 400, message = "Error in fetching  audit trail")})
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{identifier}/audit-data")
    public ResponseEntity<Object> auditTrailService(@RequestHeader(value = "Authorization") String authorization,
                                                       @PathVariable(value = "identifier") String accountIdentifier,
                                                        @RequestParam(value = "fromTime" ,required = false) String []fromTime, @RequestParam(value = "toTime",required = false) String [] toTime,
                                                        @RequestParam(value = "serviceId" ,required = false) String [] serviceId, @RequestParam(value = "applicationId",required = false) String [] applicationId,
                                                        @RequestParam(value = "activityTypeId" ,required = false) String [] activityTypeId, @RequestParam(value = "userId",required = false) String [] userId)
            throws DataProcessingException, ClientException, ServerException {
            Map<String,String[]> requestParam = new HashMap();
            requestParam.put("toTime",toTime);
            requestParam.put("fromTime",fromTime);
            requestParam.put("serviceId",serviceId);
            requestParam.put("applicationId",applicationId);
            requestParam.put("activityTypeId",activityTypeId);
            requestParam.put("userId",userId);
        UtilityBean<AuditTrailBean> utilityBean = getAuditTrailBL.clientValidation(requestParam,authorization, accountIdentifier);
        AuditTrailBean auditTrailBean = getAuditTrailBL.serverValidation(utilityBean);
        List<AuditTrailPojo> auditTrailPojoList = getAuditTrailBL.process(auditTrailBean);
        return ResponseEntity.ok().headers(headersParser.loadHeaderConfiguration()).body(auditTrailPojoList);
    }

}

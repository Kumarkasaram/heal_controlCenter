package com.heal.controlcenter.businesslogic;

import com.appnomic.appsone.common.enums.DiscoveryStatus;
import com.heal.controlcenter.beans.*;
import com.heal.controlcenter.dao.mysql.AccountsDao;
import com.heal.controlcenter.dao.mysql.AutoDiscoveryDao;
import com.heal.controlcenter.dao.mysql.ConnectionDetailsDao;
import com.heal.controlcenter.dao.mysql.ControllerDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.pojo.GetConnectionPojo;
import com.heal.controlcenter.util.CommonUtils;
import com.heal.controlcenter.util.DateTimeUtil;
import com.heal.controlcenter.util.UIMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetConnectionsBL implements BusinessLogic<Object, Integer, List<GetConnectionPojo>> {

    @Autowired
    AccountsDao accountDao;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    ControllerDao controllerDao;
    @Autowired
    ConnectionDetailsDao connectionDetailsDao;
    @Autowired
    AutoDiscoveryDao autoDiscoveryDao;
    @Autowired
    DateTimeUtil dateTimeUtil;

    @Override
    public UtilityBean<Object> clientValidation(Object requestBody, String... requestParams) throws ClientException {
        String userId;

        if (requestParams[0].isEmpty()) {
            log.error(UIMessages.AUTH_KEY_EMPTY);
            throw new ClientException(UIMessages.AUTH_KEY_EMPTY);
        }

        if (requestParams[1].isEmpty()) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
            throw new ClientException(UIMessages.ACCOUNT_IDENTIFIER_EMPTY);
        }

        try {
            userId = commonUtils.getUserId(requestParams[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while getting UserId. Details: {}", e.getMessage());
            throw new ClientException("Error occurred while fetching UserId.");
        }
        if (userId == null) {
            log.error(UIMessages.AUTH_KEY_INVALID);
            throw new ClientException(UIMessages.AUTH_KEY_INVALID);
        }

        return UtilityBean.builder()
                .authToken(requestParams[0])
                .accountIdentifier(requestParams[1])
                .userId(userId)
                .build();
    }

    @Override
    public Integer serverValidation(UtilityBean<Object> utilityBean) throws ServerException {
        String accountIdentifier = utilityBean.getAccountIdentifier();

        AccountBean account = accountDao.getAccountByIdentifier(accountIdentifier);
        if (account == null) {
            log.error(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
            throw new ServerException(UIMessages.ACCOUNT_IDENTIFIER_INVALID);
        }

        return account.getId();
    }

    @Override
    public List<GetConnectionPojo> process(Integer accountId) throws DataProcessingException {
        List<ControllerBean> controllerBeanList;
        try {
            controllerBeanList = controllerDao.getServicesList(accountId);
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }

        if (controllerBeanList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ConnectionDetailsBean> healConnectionDetailsBean = connectionDetailsDao.getConnectionsByAccountId(accountId);
        List<AutoDiscoveryDiscoveredConnectionsBean> discoveredConnectionsBeanList = autoDiscoveryDao.getDiscoveredConnectionsList();

        // From Heal UI
        List<GetConnectionPojo> connections = healConnectionDetailsBean.parallelStream()
                .map(c -> GetConnectionPojo.builder()
                        .sourceServiceId(c.getSourceId())
                        .destinationServiceId(c.getDestinationId())
                        .process(c.getIsDiscovery() == 0 ? "Manual" : "Auto")
                        .status(DiscoveryStatus.ADDED_TO_SYSTEM)
                        .lastDiscoveryRunTime(dateTimeUtil.getGMTToEpochTime(String.valueOf(c.getUpdatedTime())))
                        .build())
                .collect(Collectors.toList());

        connections.parallelStream()
                .forEach(one -> controllerBeanList.parallelStream()
                        .forEach(two -> {
                            if (one.getSourceServiceId() == two.getId()) {
                                one.setSourceServiceName(two.getName());
                                one.setSourceServiceIdentifier(two.getIdentifier());
                            }
                            if (one.getDestinationServiceId() == two.getId()) {
                                one.setDestinationServiceName(two.getName());
                                one.setDestinationServiceIdentifier(two.getIdentifier());
                            }
                        }));

        // From Auto Discovery agent
        List<GetConnectionPojo> autoDiscoConnections = discoveredConnectionsBeanList.parallelStream()
                .map(c -> GetConnectionPojo.builder()
                        .sourceServiceIdentifier(c.getSourceIdentifier())
                        .destinationServiceIdentifier(c.getDestinationIdentifier())
                        .process("Auto")
                        .status(c.getDiscoveryStatus())
                        .lastDiscoveryRunTime(c.getLastUpdatedTime())
                        .build())
                .collect(Collectors.toList());

        autoDiscoConnections.parallelStream()
                .forEach(one -> controllerBeanList.parallelStream()
                        .forEach(two -> {
                            if (one.getSourceServiceIdentifier().equals(two.getIdentifier())) {
                                one.setSourceServiceName(two.getName());
                                one.setSourceServiceId(two.getId());
                            }
                            if (one.getDestinationServiceIdentifier().equals(two.getIdentifier())) {
                                one.setDestinationServiceName(two.getName());
                                one.setDestinationServiceId(two.getId());
                            }
                        }));

        List<GetConnectionPojo> duplicates = new ArrayList<>();
        connections.parallelStream()
                .forEach(one -> autoDiscoConnections.parallelStream()
                        .forEach(two -> {
                            if (one.getSourceServiceIdentifier().equals(two.getSourceServiceIdentifier())
                                    && one.getDestinationServiceIdentifier().equals(two.getDestinationServiceIdentifier())) {
                                duplicates.add(two);
                            }
                        }));
        autoDiscoConnections.removeAll(duplicates);
        connections.addAll(autoDiscoConnections);

        connections.sort(Comparator.comparing(GetConnectionPojo::getProcess)
                .thenComparing(GetConnectionPojo::getLastDiscoveryRunTime, Comparator.reverseOrder()));

        return connections;
    }
}

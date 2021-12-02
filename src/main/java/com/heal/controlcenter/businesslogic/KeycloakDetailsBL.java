package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.JsonFileParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class KeycloakDetailsBL implements BusinessLogic<String, String, Map<String, Object>> {

    @Autowired
    JsonFileParser confProperties;

    @Autowired
    UserDao userDataDao;

    @Value("${ds.setup.type:Keycloak}")
    private String setupType;

    @Override
    public UtilityBean<String> clientValidation(String requestBody, String... requestParams) throws ClientException {
        return null;
    }

    @Override
    public String serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        return null;
    }

    @Override
    @Cacheable(value = "keycloaksettings", key = "#root.methodName")
    public Map<String, Object> process(String bean) throws DataProcessingException {
        Map<String, Object> data = confProperties.loadKeycloakSSOConfig();
        String setup = userDataDao.getHealSetup();
        if (setup == null) {
            log.error("Unable to fetch setup type");
        } else {
            data.put("isActiveDirectory", setupType.equalsIgnoreCase(setup));
        }
        return data;
    }
}

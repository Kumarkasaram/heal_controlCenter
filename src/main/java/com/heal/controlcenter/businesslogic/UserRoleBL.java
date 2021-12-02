package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.IdBean;
import com.heal.controlcenter.beans.UtilityBean;
import com.heal.controlcenter.dao.mysql.UserDao;
import com.heal.controlcenter.exception.ClientException;
import com.heal.controlcenter.exception.ControlCenterException;
import com.heal.controlcenter.exception.DataProcessingException;
import com.heal.controlcenter.exception.ServerException;
import com.heal.controlcenter.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Sourav Suman - 18-10-2021
 */
@Service
@Slf4j
public class UserRoleBL implements BusinessLogic<String, String, List<IdBean>> {

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    UserDao userRolesAndProfilesDao;

    @Override
    public UtilityBean<String> clientValidation(String requestBody, String... requestParams) throws ClientException {
        String userId;
        try {
            userId = commonUtils.getUserId(requestParams[0]);
        } catch (ControlCenterException e) {
            log.error("Exception encountered while fetching userId. Reason: {}", e.getMessage(), e);
            throw new ClientException("Error while fetching userId from the Authorization token");
        }

        if (null == userId) {
            log.error("Invalid Authorization token. Reason: UserId is NULL.");
            throw new ClientException("Invalid Authorization token");
        }

        return UtilityBean.<String>builder()
                .pojoObject(userId)
                .accountIdentifier(userId)
                .authToken(requestParams[0])
                .build();
    }

    @Override
    public String serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        return null;
    }

    @Override
    public List<IdBean> process(String bean) throws DataProcessingException {
        List<IdBean> data;
        try {
            log.debug("getting role details");
            data = userRolesAndProfilesDao.getRoles();
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }
        if (Objects.isNull(data) || data.isEmpty()) {
            throw new DataProcessingException("User roles information unavailable");
        }

        log.info("User roles information fetched successfully");

        return data;
    }

}

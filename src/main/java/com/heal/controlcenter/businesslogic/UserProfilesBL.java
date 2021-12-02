package com.heal.controlcenter.businesslogic;

import com.heal.controlcenter.beans.UserProfileBean;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sourav Suman - 20-10-2021
 */

@Slf4j
@Service
public class UserProfilesBL implements BusinessLogic<String, String, List<UserProfileBean>>{

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

        if(null == userId) {
            log.error("Invalid Authorization token. Reason: UserId is NULL.");
            throw new ClientException("Invalid Authorization token");
        }

        return UtilityBean.<String>builder()
                .pojoObject(userId)
                .build();
    }

    @Override
    public String serverValidation(UtilityBean<String> utilityBean) throws ServerException {
        return null;
    }

    @Override
    public List<UserProfileBean> process(String bean) throws DataProcessingException {
        try {
            List<UserProfileBean> listofUserProfiles = userRolesAndProfilesDao.getUserProfiles();
            for (UserProfileBean userProfile: listofUserProfiles) {
                Set<String> setOfUserProfileMapping = new HashSet<>(userRolesAndProfilesDao.getAccessProfileMapping(userProfile.getUserProfileId()));
                userProfile.setAccessibleFeatures(setOfUserProfileMapping);
            }
            return listofUserProfiles;
        } catch (ControlCenterException e) {
            throw new DataProcessingException(e.getMessage());
        }
    }
}
